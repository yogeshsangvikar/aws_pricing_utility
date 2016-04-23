package com.my.aws.cronjob;

import com.my.aws.pojo.EC2InstancePrice;
import com.my.aws.utils.LoadAWSPriceJS;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

/**
 * Created by Yogesh.Sangvikar on 5/28/2015.
 * Fetch AWS EC2 instance price for regions us-east-1, us-west-1, us-west-2
 */
public class AwsPriceJob implements Job{

    public static Logger logger = Logger.getLogger(AwsPriceJob.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.info("Running  AwsPriceJob");
        Properties props = new Properties();
        InputStream input = AwsPriceJob.class.getClassLoader().getResourceAsStream("aws_resource.properties");

        if(input == null){
            logger.error("failed to load aws_resource.properties file. ");
            return;
        }

        try {
            props.load(input);
            loadEc2Prices(props);

        } catch (IOException e) {
            logger.error("Error while processing AWS pricing.", e);
        } catch (Exception e) {
            logger.error("Error while processing AWS pricing.", e);
        }
    }

    private void loadEc2Prices(Properties props) throws Exception{
        Date currentTime = new Date();
        LoadAWSPriceJS awsPriceJS = new LoadAWSPriceJS();
        // NEW Generation
        ArrayList<String> awsPriceLinks = new ArrayList<String>();
        awsPriceLinks.add(props.getProperty("AWS_ON_DEMAND_LINUX"));
        awsPriceLinks.add(props.getProperty("AWS_ON_DEMAND_MSWIN"));
        awsPriceLinks.add(props.getProperty("AWS_ON_DEMAND_RHEL"));
        awsPriceLinks.add(props.getProperty("AWS_ON_DEMAND_SLES"));
        awsPriceLinks.add(props.getProperty("AWS_ON_DEMAND_MSWIN_SQL"));
        awsPriceLinks.add(props.getProperty("AWS_ON_DEMAND_MSWIN_SQL_WEB"));

        // Previous Generation
        awsPriceLinks.add(props.getProperty("AWS_ON_DEMAND_LINUX_PREV_GEN"));
        awsPriceLinks.add(props.getProperty("AWS_ON_DEMAND_MSWIN_PREV_GEN"));
        awsPriceLinks.add(props.getProperty("AWS_ON_DEMAND_RHEL_PREV_GEN"));
        awsPriceLinks.add(props.getProperty("AWS_ON_DEMAND_SLES_PREV_GEN"));
        awsPriceLinks.add(props.getProperty("AWS_ON_DEMAND_MSWIN_SQL_PREV_GEN"));
        awsPriceLinks.add(props.getProperty("AWS_ON_DEMAND_MSWIN_SQL_WEB_PREV_GEN"));

        System.out.println("### - "+awsPriceLinks);

        int i = 1;
        for(String link : awsPriceLinks){
            String jsonStr = awsPriceJS.loadAWSPriceJS(link);
            ArrayList<JSONObject> instacesPriceList = awsPriceJS.getInstanceJSON(jsonStr);

            System.out.println("## Price Link - ["+link+"]");
            for(JSONObject instance : instacesPriceList){
                EC2InstancePrice instancePrice = awsPriceJS.generateEC2Instance(instance);
                instancePrice.setLastupdated(currentTime);

                System.out.println(i++ +" : "+instancePrice);
            }
        }
    }

    public static void main(String ... args) throws Exception{
        Properties props = new Properties();
        InputStream input = AwsPriceJob.class.getClassLoader().getResourceAsStream("aws_resource.properties");
        props.load(input);

        AwsPriceJob priceJob = new AwsPriceJob();
        priceJob.loadEc2Prices(props);
    }
}
