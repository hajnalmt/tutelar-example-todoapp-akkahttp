FROM hseeberger/scala-sbt:8u222_1.3.5_2.13.1 as builder
COPY build.sbt /app/build.sbt
COPY project /app/project
WORKDIR /app
RUN sbt update test:update
COPY . .
RUN sbt compile stage


FROM openjdk:8
WORKDIR /app
COPY --from=builder /app/target/universal/stage /app
USER root
RUN useradd --system --create-home --uid 1001 --gid 0 tutelar && \
    chmod -R u=rX,g=rX /app && \
    chmod u+x,g+x /app/bin/tutelar-example-todoapp-akkahttp && \
    chown -R 1001:root /app
USER 1001

EXPOSE 9000
ENTRYPOINT ["/app/bin/tutelar-example-todoapp-akkahttp"]
CMD []
