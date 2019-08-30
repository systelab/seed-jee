# Metrics

Metrics are generated using [MicroProfile](https://microprofile.io), that helps providing a unified way for servers to export Monitoring data ("Telemetry") to management agents.

Refer to the official [MicroProfile metrics documentation](https://github.com/eclipse/microprofile-metrics/blob/master/spec/src/main/asciidoc/metrics_spec.adoc) in order to get detailed information.

Metrics are generated in OpenMetrics text format to be compatible with Prometheus. Check out the [prometheus-monitoring repository](https://github.com/systelab/prometheus-monitoring) in order to start Prometheus and Grafana.

> Prometheus configuration should be changed in order to setup the endpoints serving the metrics.

## REST endpoints

### Required Base metrics
Required base metrics are exposed under /metrics/base.

### Application metrics
Application specific metrics are exposed under /metrics/application.

### Vendor specific Metrics
Vendor specific metrics are exposed under /metrics/application.
