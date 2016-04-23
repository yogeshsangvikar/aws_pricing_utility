package com.my.aws.utils;


import com.my.aws.pojo.AwsConstants;
import com.my.aws.pojo.EC2InstancePrice;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Yogesh.Sangvikar on 5/11/2015.
 */
public class LoadAWSPriceJS {
    //public static String REGION = "us-west-2";

    public String loadAWSPriceJS(String url_txt) throws IOException {
        InputStream in = new URL(url_txt).openStream();
        String content = IOUtils.toString(in);

        // Replace /*...*/
        Pattern pattern = Pattern.compile("\\/\\*[^\\x00]+\\*\\/");
        Matcher matcher = pattern.matcher(content);
        content = matcher.replaceAll("");

        // Add quotes around the Key String
        pattern = Pattern.compile("([a-zA-Z0-9.]+):");
        matcher = pattern.matcher(content);
        final StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String str = "\""+"$1"+"\":";
            matcher.appendReplacement(sb, str);
        }
        matcher.appendTail(sb);
        content = sb.toString();

        // / Replace ";" with new line "\n"
        pattern = Pattern.compile(";");
        matcher = pattern.matcher(content);
        content = matcher.replaceAll("\n");

        // Remove the callback function syntax - Start
        pattern = Pattern.compile("callback\\(");
        matcher = pattern.matcher(content);
        content = matcher.replaceAll("");

        // Remove the callback function syntax - End
        pattern = Pattern.compile("\\)$");
        matcher = pattern.matcher(content);
        content = matcher.replaceAll("");

        return content;
    }

    public ArrayList<JSONObject> getInstanceJSON(String jsonString) throws ParseException {
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(jsonString);
        JSONObject jsonObject = (JSONObject) obj;

        JSONObject configObj = (JSONObject)jsonObject.get("config");
        JSONArray regionArray = (JSONArray)configObj.get("regions");
        Iterator<JSONObject> iterator = regionArray.iterator();

        ArrayList<JSONObject> jsonRegionObjList = new ArrayList<JSONObject>();

        while(iterator.hasNext()){
            JSONObject regionObj = iterator.next();

            /* Filter particular region price JSONs */
            String region_str = (String) regionObj.get("region");
            if (region_str.equalsIgnoreCase(AwsConstants.US_WEST_2_REGION) || region_str.equalsIgnoreCase(AwsConstants.US_WEST_1_REGION)
                    || region_str.equalsIgnoreCase(AwsConstants.US_EAST_1_REGION)){
                jsonRegionObjList.add(regionObj);
            }
      }

        ArrayList<JSONObject> instanceBySizeList = new ArrayList<JSONObject>();

        for(JSONObject jsonRegionObj : jsonRegionObjList) {
            ArrayList<JSONObject> instancesBySizesList = new ArrayList<JSONObject>();
            ArrayList<JSONObject> instanceBySizeList1 = new ArrayList<JSONObject>();

            //System.out.println("Processing region - "+jsonRegionObj.get("region").toString());
            String region_str = jsonRegionObj.get("region").toString();

            JSONArray instanceTypes = (JSONArray) jsonRegionObj.get("instanceTypes");
            iterator = instanceTypes.iterator();

            while (iterator.hasNext()) {
                JSONObject instances = iterator.next();

                instancesBySizesList.add(instances);
            }

            for (JSONObject inObject : instancesBySizesList) {
                JSONArray instancesBySize1 = (JSONArray) inObject.get("sizes");

                iterator = instancesBySize1.iterator();
                while (iterator.hasNext()) {
                    JSONObject instance = iterator.next();
                    instance.put("region", region_str);
                    //System.out.println("instance - " +instance.toString());
                    instanceBySizeList.add(instance);
                }
            }
        }
        return instanceBySizeList;
    }

    public EC2InstancePrice generateEC2Instance(JSONObject ec2InstanceJson){

        System.out.println("Price JSON - "+ec2InstanceJson);

        EC2InstancePrice ec2InstancePrice = new EC2InstancePrice();

        String instaceType = (String) ec2InstanceJson.get("size");
        String region = ec2InstanceJson.get("region").toString();

        JSONArray innerJson = (JSONArray) ec2InstanceJson.get("valueColumns");
        JSONObject priceObject = (JSONObject) innerJson.get(0);

        String os = (String) priceObject.get("name");

        Double price = 0.0;
        try {
            price = Double.parseDouble((String) ((JSONObject) priceObject.get("prices")).get("USD"));

        } catch (NumberFormatException e){
            System.out.println("NumberFormatException for Price JSON -"+ec2InstanceJson+"["+e.getMessage()+"]");
        }

        ec2InstancePrice.setRegion(region);
        ec2InstancePrice.setInstanceType(instaceType);
        ec2InstancePrice.setOs(os);
        ec2InstancePrice.setPrice(price);
        return ec2InstancePrice;
    }
}
