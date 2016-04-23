package com.my.aws;

import com.my.aws.cronjob.AwsPriceJob;
import org.apache.log4j.Logger;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

import java.io.InputStream;
import java.text.ParseException;
import java.util.Properties;
import java.util.Set;

/**
 * Created by Yogesh.Sangvikar on 5/19/2015.
 *  Added Quartz scheduler to keep the instance price details updated.
 */
public class AwsPricingModule {

    public static Logger logger = Logger.getLogger(AwsPricingModule.class);

    public AwsPricingModule() {

    }

    public static void configureJob(){

        logger.info("Scheduling AWS Spend Cron Jobs");

        // Schedule AWS Price Job
        JobDetail awsJob = new JobDetail();
        awsJob.setName("LoadAwsPricesJob");
        awsJob.setJobClass(AwsPriceJob.class);

        CronTrigger awsTrigger = new CronTrigger();
        awsTrigger.setName("LoadAwsPricesTrigger");

        try {

            // Run on 1st day of Month at Mid night
            awsTrigger.setCronExpression("0 0 1 * * ?");
            //awsTrigger.setCronExpression("0 */10 * * * ?");

            //schedule it
            Scheduler awsScheduler = new StdSchedulerFactory().getScheduler();
            awsScheduler.start();
            awsScheduler.scheduleJob(awsJob, awsTrigger);

        } catch (ParseException e) {
            logger.error("Error occurred while parsing cron job - ",e);
        } catch (SchedulerException e) {
            logger.error("Error occurred while scheduling cron job - ",e);
        }
    }

    public Set<Object> getInstancesPriceList() throws Exception {

        Properties props = new Properties();
        InputStream input = AwsPricingModule.class.getClassLoader().getResourceAsStream("aws_resource.properties");

        if(input == null){
            logger.error("failed to load aws_resource.properties file. ");
            return null;
        }

        props.load(input);

        /*DbUtils dbUtils = new DbUtils(props);


        LoadAWSInstances awsInstances = new LoadAWSInstances(props);

        Set<Instance> instances = awsInstances.listAllInstances();
        Set<EC2Instance> ec2Instances = new HashSet<EC2Instance>();

        for(Instance instance : instances){

            EC2Instance ec2Instance = new EC2Instance( instance.getInstanceId(), instance.getInstanceType(),
                    instance.getLaunchTime(), instance.getTags(), instance.getState(),
                    instance.getPlacement().getAvailabilityZone(), instance.getStateTransitionReason());

            //Calculate Cost
            Double perHourCost = dbUtils.getEc2InstancePrice(AwsConstants.US_WEST_2_REGION, ec2Instance.getInstanceType(), ec2Instance.getOperatingSystem());
            Double totalCost = perHourCost * ec2Instance.getActiveTime();

            ec2Instance.setTotalCost(totalCost);
            ec2Instance.setPerHrCost(perHourCost);

            //Store the instance details in DB
            //dbUtils.storeInstanceDetails(ec2Instance);

            ec2Instances.add(ec2Instance);
        }*/

        /*DbUtils dbUtils = new DbUtils(props);
        Set<EC2Instance> ec2Instances = dbUtils.getInstanceDetails();*/

        return null;
    }

    public static void main(String [] args) throws Exception {

    }

}