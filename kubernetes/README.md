# Kubernetes Deployment and Service for Digital Bank

This project contains Kubernetes configurations for deploying a digital banking application. It includes services for Kafka, Kafdrop, PostgreSQL, an API Gateway, and a User Service.

## Project Structure

- **base/**: Contains the base Kubernetes manifests for each service.
  - **kafka/**: Deployment, service, and config map for Kafka.
  - **kafdrop/**: Deployment and service for Kafdrop UI.
  - **postgres/**: Deployment, service, PVC, and secret for PostgreSQL.
  - **api-gateway/**: Deployment and service for the API Gateway.
  - **user-service/**: Deployment and service for the User Service.
  - **namespace.yaml**: Defines the namespace for the application.
  - **account-service/**: Deployment and service for the Account Service.  

- **overlays/**: Contains environment-specific configurations.
  - **dev/**: Development environment configurations.
  - **prod/**: Production environment configurations.

## Setup Instructions

1. **Install Kubernetes**: Ensure you have a Kubernetes cluster running. You can use Minikube, Kind, or a cloud provider.

2. **Apply Namespace**: Create the namespace for the application.
   ```
   kubectl apply -f base/namespace.yaml
   ```

3. **Deploy Services**: Apply the base configurations for all services.
   ```
   kubectl apply -k base/
   ```

4. **Access Services**: Use the exposed ports to access the services. For example, Kafdrop can be accessed at `http://<your-cluster-ip>:9000`.

## Usage Guidelines

- Modify the environment variables in the deployment files as needed for your configuration.
- Use the `overlays` directory to customize configurations for different environments (development, production).
- Ensure that persistent storage is configured correctly for PostgreSQL.

## Additional Resources

- [Kubernetes Documentation](https://kubernetes.io/docs/home/)
- [Kafka Documentation](https://kafka.apache.org/documentation/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)


## Added by Matiul Alam

# To create resources, go to the folder "Digital-bank\kubernetes\overlays\prod>" and then run the command

   ```
kubectl apply -k .
   ```

# To get the "user-service" URL and dynamic port from the namespace "digital-bank"


   ```
minikube service user-service -n digital-bank

   ```
 
# To get the "account-service" URL and dynamic port from the namespace "digital-bank"


   ```
minikube service account-service -n digital-bank

   ``` 

# To see all the pods  from the namespace "digital-bank"

   ```
kubectl get pods -A

   ```
# To see all the services from the namespace "digital-bank"

   ```
kubectl get svc -n digital-bank

   ```

# To delete all the resources from the namespace "digital-bank"

   ```
kubectl delete all --all -n digital-bank

   ```

## Deployment using "Kind"

# To see all the services from the namespace "digital-bank"

   ```
kind create cluster --config=overlays/prod/kind-config.yaml

   ```

# To create an ingress controller

   ```
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/kind/deploy.yaml

   ```
# To create Kubernetes  objects (wait few seconds)

   ```
kubectl apply -k overlays/prod

   ```

# To delete the cluster

   ```
kind delete cluster

   ```
# Install Istio
istioctl install --set values.defaultRevision=default -y

# Deploy using the new Istio overlay
kubectl apply -k kubernetes/overlays/istio/

# Verify namespace has sidecar injection enabled
kubectl get namespace digital-bank --show-labels

# Check that pods now have 2 containers (app + istio-proxy)
kubectl get pods -n digital-bank

# Install Istio addons
kubectl apply -f https://raw.githubusercontent.com/istio/istio/release-1.22/samples/addons/prometheus.yaml
kubectl apply -f https://raw.githubusercontent.com/istio/istio/release-1.22/samples/addons/grafana.yaml
kubectl apply -f https://raw.githubusercontent.com/istio/istio/release-1.22/samples/addons/jaeger.yaml
kubectl apply -f https://raw.githubusercontent.com/istio/istio/release-1.22/samples/addons/kiali.yaml


# Port forward for local access
kubectl port-forward svc/istio-ingressgateway -n istio-system 8080:80

# Access Kiali dashboard
kubectl port-forward svc/kiali -n istio-system 20001:20001

# Access Grafana
kubectl port-forward svc/grafana -n istio-system 3000:3000
