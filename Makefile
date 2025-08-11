.PHONY: podman-login build-user build-product build-order push-user push-product push-order \
        build-and-push run-db-only run-services-only stop clean restart logs logs-db-only logs-rabbitmq

# ====== Docker Hub Credentials ======
DOCKER_USERNAME = sushankk
DOCKER_PASSWORD = your_password_here

# ====== Images ======
USER_IMAGE    = docker.io/$(DOCKER_USERNAME)/ecom-user:latest
PRODUCT_IMAGE = docker.io/$(DOCKER_USERNAME)/ecom-product:latest
ORDER_IMAGE   = docker.io/$(DOCKER_USERNAME)/ecom-order:latest

# ====== Podman Login ======
podman-login:
	podman login docker.io -u $(DOCKER_USERNAME) -p $(DOCKER_PASSWORD)

# ====== Build Images ======
build-user:
	podman build -t $(USER_IMAGE) ./user

build-product:
	podman build -t $(PRODUCT_IMAGE) ./product

build-order:
	podman build -t $(ORDER_IMAGE) ./order

# ====== Push Images ======
push-user:
	podman push $(USER_IMAGE)

push-product:
	podman push $(PRODUCT_IMAGE)

push-order:
	podman push $(ORDER_IMAGE)

# ====== Build & Push All ======
build-and-push: build-user build-product build-order push-user push-product push-order

# ====== Run Only DB + RabbitMQ Containers for Local Dev ======
run-db-only:
	podman-compose -f podman-compose.db.yml up -d

# ====== Run Only Services (Images Must Already Be Built/Pushed) ======
run-services-only:
	podman-compose -f podman-compose.services.yml up -d

# ====== Stop All Containers ======
stop:
	podman-compose -f podman-compose.db.yml down
	podman-compose -f podman-compose.services.yml down

# ====== Restart Services Only ======
restart:
	$(MAKE) stop
	$(MAKE) run-services-only

# ====== Logs ======
logs:
	podman-compose -f podman-compose.services.yml logs -f

logs-db-only:
	podman-compose -f podman-compose.db.yml logs -f

logs-rabbitmq:
	podman logs -f ecom-rabbitmq

# ====== Clean All Volumes ======
clean:
	podman-compose -f podman-compose.db.yml down -v
