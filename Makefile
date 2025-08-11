.PHONY: docker-login build-user build-product build-order push-user push-product push-order \
        build-and-push run-db-only run-services-only run-monitoring stop stop-monitoring \
        restart restart-services logs logs-db-only logs-rabbitmq logs-monitoring clean clean-all \
        ensure-network

# ====== Docker Hub Credentials ======
DOCKER_USERNAME = sushankk
DOCKER_PASSWORD = hisushank5125

# ====== Images ======
USER_IMAGE    = docker.io/$(DOCKER_USERNAME)/ecom-user:latest
PRODUCT_IMAGE = docker.io/$(DOCKER_USERNAME)/ecom-product:latest
ORDER_IMAGE   = docker.io/$(DOCKER_USERNAME)/ecom-order:latest

# ====== Networking ======
ensure-network:
	@docker network inspect ecom-network >/dev/null 2>&1 || \
	 docker network create ecom-network

# ====== Authentication ======
docker-login:
	@echo $(DOCKER_PASSWORD) | docker login -u $(DOCKER_USERNAME) --password-stdin

# ====== Build Images ======
build-user:
	@docker build -t $(USER_IMAGE) ./user

build-product:
	@docker build -t $(PRODUCT_IMAGE) ./product

build-order:
	@docker build -t $(ORDER_IMAGE) ./order

# ====== Push Images ======
push-user:
	@docker push $(USER_IMAGE)

push-product:
	@docker push $(PRODUCT_IMAGE)

push-order:
	@docker push $(ORDER_IMAGE)

# ====== Build & Push All ======
build-and-push: build-user build-product build-order push-user push-product push-order

# ====== Run Containers ======
run-db-only: ensure-network
	@docker compose -f infra/compose/docker-compose-db.yml up -d

run-services-only: ensure-network
	@docker compose -f infra/compose/docker-compose-services.yml up -d

run-monitoring: ensure-network
	@docker compose -f infra/compose/docker-compose-monitoring.yml up -d

# ====== Stop Containers ======
stop:
	@docker compose -f infra/compose/docker-compose-db.yml down
	@docker compose -f infra/compose/docker-compose-services.yml down

stop-monitoring:
	@docker compose -f infra/compose/docker-compose-monitoring.yml down

# ====== Restart ======
restart-services: stop run-services-only

# ====== Logs ======
logs:
	@docker compose -f infra/compose/docker-compose-services.yml logs -f

logs-db-only:
	@docker compose -f infra/compose/docker-compose-db.yml logs -f

logs-rabbitmq:
	@docker logs -f ecom-rabbitmq

logs-monitoring:
	@docker compose -f infra/compose/docker-compose-monitoring.yml logs -f

# ====== Cleanup ======
clean:
	@docker compose -f infra/compose/docker-compose-db.yml down -v
	@docker compose -f infra/compose/docker-compose-services.yml down -v
	@docker compose -f infra/compose/docker-compose-monitoring.yml down -v

clean-all: clean
	@docker volume prune -f
	@docker network rm ecom-network || true
