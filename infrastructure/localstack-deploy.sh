#!/bin/bash
set -e # Stops the script if any command fails

# on real aws we can just redeploy the new cloud formation stack, but this is not supported by localstack - so we delete
aws --endpoint-url=http://localhost:4566 cloudformation delete-stack \
    --stack-name patient-management

# localstack endpoint-url mentioned, otherwise aws cmd will try to execute against real aws instance
aws --endpoint-url=http://localhost:4566 cloudformation deploy \
    --stack-name patient-management \
    --template-file "./cdk.out/localstack.template.json"

# print load balancer address
aws --endpoint-url=http://localhost:4566 elbv2 describe-load-balancers \
    --query "LoadBalancers[0].DNSName" --output text