package com.ljy.Registers;

import org.springframework.context.SmartLifecycle;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

//得到所用要注册的实例注册并且注册
public class LjyRegisterService implements SmartLifecycle {

    private static final String SEND_BEAT_URL = "/instance/beat";
    private static final long DEFAULT_DUTATION = 5;
    List<ServiceSpecial> instances=new ArrayList<>();

    RestTemplate template=new RestTemplate();
    public static String WEB_CONTEXT = "/nacos";

    public static String NACOS_URL_BASE = WEB_CONTEXT + "/v1/ns";
    public static String NACOS_URL_INSTANCE = NACOS_URL_BASE + "/instance";
    private ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(5, new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            thread.setName("com.ljy.beat.sender");
            return thread;
        }
    });
    @Override
    public void start() {
        for(ServiceSpecial serviceSpecial:instances){
          sendBeat(serviceSpecial);
        }

    }

    private void sendBeat(ServiceSpecial serviceSpecial) {
        BeatInfo beatInfo=creatbeatinfo(serviceSpecial);
        executorService.schedule(new Tasksendbeat(beatInfo),beatInfo.getPeriod(), TimeUnit.SECONDS);
    }

    private BeatInfo creatbeatinfo(ServiceSpecial serviceSpecial) {
      BeatInfo beatInfo=new BeatInfo();
      beatInfo.setIp(serviceSpecial.getAddress());
      beatInfo.setPort(serviceSpecial.getPort());
      beatInfo.setPeriod(DEFAULT_DUTATION);//默认设置5秒钟发送一次心跳
      beatInfo.setServiceName(serviceSpecial.getServicename());
      beatInfo.setCluster("defalut");
      beatInfo.setWeight(4);
      beatInfo.setScheduled(false);
      return  beatInfo;

    }

    public void register(){
       for(ServiceSpecial serviceSpecial:instances){
           register(serviceSpecial);
       }
    }

    private void register(ServiceSpecial serviceSpecial) {
        final Map<String, String> params = new HashMap<String, String>(9);
        params.put("namespaceId","default");
        params.put("serviceName",serviceSpecial.getServicename());
        params.put("groupName","");
        params.put("ClusterName","");
        params.put("ip", serviceSpecial.getAddress());
        params.put("port", String.valueOf(serviceSpecial.getPort()));
        params.put("weight", String.valueOf(4));
        params.put("enable", String.valueOf(true));
        params.put("healthy", String.valueOf(true));
        params.put("ephemeral", String.valueOf(true));
        params.put("metadata", "");
        String url="http://"+serviceSpecial.getAddress()+":"+serviceSpecial.getPort()
                +NACOS_URL_INSTANCE;
        String result=template.postForObject(url,null,String.class,params);
    }

    @Override
    public void stop() {

    }

    @Override
    public boolean isRunning() {
        return false;
    }
    public List<ServiceSpecial> getInstances(){
        return this.instances;
    }
    public void setInstances(List<ServiceSpecial> list){
        instances=list;
    }

    private class Tasksendbeat implements Runnable{
        BeatInfo beatInfo;
public  Tasksendbeat(BeatInfo beatInfo){
    this.beatInfo=beatInfo;
}
        @Override
        public void run() {
            if (beatInfo.isStopped()) {
                return;
            }
            String url="http://"+beatInfo.getIp()+":"+beatInfo.getPort()
                    +NACOS_URL_BASE+SEND_BEAT_URL;
            Map<String, String> params = new HashMap<String, String>(4);
            params.put("beat", JSON.toJSONString(beatInfo));
            params.put("namespaceId","default");
            params.put("ServiceName",beatInfo.getServiceName());
            String jsonobject =template.getForObject(url,String.class,params);
            JSONObject jsonObject = JSON.parseObject(jsonobject);
            long result=jsonObject.getLong("clientBeatInterval");
            long nextTime = result > 0 ? result : beatInfo.getPeriod();
            executorService.schedule(new Tasksendbeat(beatInfo), nextTime, TimeUnit.MILLISECONDS);
        }
    }
}
