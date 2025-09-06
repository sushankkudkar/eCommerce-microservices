.PHONY: nerdctl-login build-user build-product build-order build-notification build-config build-eureka build-gateway \
        push-user push-product push-order push-notification push-config push-eureka push-gateway \
        build-and-push run-db-only run-services-only run-monitoring stop stop-monitoring \
        restart restart-services logs logs-db logs-rabbitmq logs-monitoring clean clean-all \
        ensure-network ps

# ====== Docker Hub Credentials ======
DOCKER_USERNAME = sushankk
DOCKER_PASSWORD = hisushank5125

# ====== Images ======
USER_IMAGE          = docker.io/$(DOCKER_USERNAME)/ecom-user:latest
PRODUCT_IMAGE       = docker.io/$(DOCKER_USERNAME)/ecom-product:latest
ORDER_IMAGE         = docker.io/$(DOCKER_USERNAME)/ecom-order:latest
NOTIFICATION_IMAGE  = docker.io/$(DOCKER_USERNAME)/ecom-notification:latest
CONFIG_IMAGE        = docker.io/$(DOCKER_USERNAME)/ecom-config-server:latest
EUREKA_IMAGE        = docker.io/$(DOCKER_USERNAME)/ecom-eureka:latest
GATEWAY_IMAGE       = docker.io/$(DOCKER_USERNAME)/ecom-gateway:latest

# ====== Authentication ======
nerdctl-login:
	@echo $(DOCKER_PASSWORD) | nerdctl login -u $(DOCKER_USERNAME) --password-stdin

# ====== Build Images ======
build-user:
	@nerdctl build --platform linux/arm64 -t $(USER_IMAGE) ./user

build-product:
	@nerdctl build --platform linux/arm64 -t $(PRODUCT_IMAGE) ./product

build-order:
	@nerdctl build --platform linux/arm64 -t $(ORDER_IMAGE) ./order

build-notification:
	@nerdctl build --platform linux/arm64 -t $(NOTIFICATION_IMAGE) ./notification

build-config:
	@nerdctl build --platform linux/arm64 -t $(CONFIG_IMAGE) ./config-server

build-eureka:
	@nerdctl build --platform linux/arm64 -t $(EUREKA_IMAGE) ./eureka

build-gateway:
	@nerdctl build --platform linux/arm64 -t $(GATEWAY_IMAGE) ./gateway

# ====== Push Images ======
push-user:
	@nerdctl push $(USER_IMAGE)

push-product:
	@nerdctl push $(PRODUCT_IMAGE)

push-order:
	@nerdctl push $(ORDER_IMAGE)

push-notification:
	@nerdctl push $(NOTIFICATION_IMAGE)

push-config:
	@nerdctl push $(CONFIG_IMAGE)

push-eureka:
	@nerdctl push $(EUREKA_IMAGE)

push-gateway:
	@nerdctl push $(GATEWAY_IMAGE)

# ====== Build & Push All ======
build-and-push: build-user build-product build-order build-notification build-config build-eureka build-gateway \
                push-user push-product push-order push-notification push-config push-eureka push-gateway

# ====== Ensure Network ======
ensure-network:
	@nerdctl network inspect ecom-network >/dev/null 2>&1 || nerdctl network create ecom-network

# ====== Run Databases ======
run-db-only: ensure-network
	nerdctl compose -f infra/compose/docker-compose-db.yml up -d

# ====== Run Services ======
run-services-only: ensure-network
	nerdctl compose -f infra/compose/docker-compose-services.yml up -d

# ====== Run Monitoring ======
run-monitoring: ensure-network
	nerdctl compose -f infra/compose/docker-compose-monitoring.yml up -d

# ====== Stop Containers ======
stop: ensure-network
	nerdctl compose -f infra/compose/docker-compose-db.yml down
	nerdctl compose -f infra/compose/docker-compose-services.yml down

stop-monitoring: ensure-network
	nerdctl compose -f infra/compose/docker-compose-monitoring.yml down

# ====== Restart Services ======
restart-services: stop run-services-only

# ====== Logs ======
logs-db: ensure-network
	nerdctl compose -f infra/compose/docker-compose-db.yml logs -f

logs: ensure-network
	nerdctl compose -f infra/compose/docker-compose-services.yml logs -f

logs-monitoring: ensure-network
	nerdctl compose -f infra/compose/docker-compose-monitoring.yml logs -f

logs-rabbitmq: ensure-network
	nerdctl compose -f infra/compose/docker-compose-db.yml logs -f rabbitmq

# ====== Cleanup ======
clean: ensure-network
	nerdctl compose -f infra/compose/docker-compose-db.yml down
	nerdctl compose -f infra/compose/docker-compose-monitoring.yml down
	nerdctl compose -f infra/compose/docker-compose-services.yml down

clean-all:
	# Stop and remove all containers, even if running
	-nerdctl rm -f $$(nerdctl ps -aq) || true

	# Remove all volumes
	-nerdctl volume rm $$(nerdctl volume ls -q) || true

	# Remove all networks (except default ones like bridge/host/none)
	-nerdctl network rm $$(nerdctl network ls -q | grep -v -E "bridge|host|none") || true

	# Run down for each compose file just in case
	-nerdctl compose -f infra/compose/docker-compose-db.yml down -v --remove-orphans || true
	-nerdctl compose -f infra/compose/docker-compose-monitoring.yml down -v --remove-orphans || true
	-nerdctl compose -f infra/compose/docker-compose-services.yml down -v --remove-orphans || true

# ====== Status ======
ps: ensure-network
	nerdctl compose -f infra/compose/docker-compose-db.yml ps
	nerdctl compose -f infra/compose/docker-compose-monitoring.yml ps
	nerdctl compose -f infra/compose/docker-compose-services.yml ps

