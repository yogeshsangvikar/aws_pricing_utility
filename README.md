# aws_pricing_utility
Utility to extract &amp; publish AWS instance prices using the AWS price links 

This utility is used to extract the AWS instances prices based on Region, Instance Type, operation system & version.

### Utility Features :
1.  The utility is packaged as Jar.
2.  It has quartz scheduler configuration to get scheduled AWS price updates.
3.  The Utility extract the AWS instance price details from price links like (AWS_ON_DEMAND_LINUX=http://a0.awsstatic.com/pricing/1/ec2/linux-od.min.js).
4.  The aws entity price details can be expanded to load details to DB & use the details for Custom AWS spend reporting.
