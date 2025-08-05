#!/bin/bash

# Memory monitoring script for Spring Boot application
# Bu script uygulamanın memory kullanımını izler ve raporlar

echo "Spring Boot Memory Monitoring - $(date)"
echo "=========================================="

# Docker container memory stats
if command -v docker &> /dev/null; then
    echo "Docker Container Memory Usage:"
    docker stats --no-stream --format "table {{.Name}}\t{{.MemUsage}}\t{{.MemPerc}}" | grep scrum-tools
    echo ""
fi

# JVM heap dump alma (gerektiğinde)
create_heap_dump() {
    local container_name="scrum-tools-app"
    local dump_file="/tmp/heap-dump-$(date +%Y%m%d-%H%M%S).hprof"

    echo "Creating heap dump..."
    docker exec $container_name jcmd 1 GC.run_finalization
    docker exec $container_name jcmd 1 VM.gc
    docker exec $container_name jcmd 1 GC.dump_heap $dump_file
    echo "Heap dump created: $dump_file"
}

# Memory kullanımı threshold check
check_memory_threshold() {
    local threshold=80
    local current_usage=$(docker stats --no-stream --format "{{.MemPerc}}" scrum-tools-app | sed 's/%//')

    if (( $(echo "$current_usage > $threshold" | bc -l) )); then
        echo "WARNING: Memory usage is above ${threshold}%: ${current_usage}%"
        echo "Consider investigating memory leaks or increasing memory limits"
        return 1
    else
        echo "Memory usage is healthy: ${current_usage}%"
        return 0
    fi
}

# Ana monitoring fonksiyonu
monitor_memory() {
    while true; do
        clear
        echo "Spring Boot Memory Monitoring - $(date)"
        echo "=========================================="

        # Container stats
        docker stats --no-stream scrum-tools-app scrum-tools-postgres

        echo ""
        echo "Application Health Check:"
        curl -s http://localhost:8080/actuator/health | jq '.'

        echo ""
        echo "JVM Memory Metrics:"
        curl -s http://localhost:8080/actuator/metrics/jvm.memory.used | jq '.measurements[0].value'

        check_memory_threshold

        sleep 30
    done
}

# Script arguments
case "$1" in
    "monitor")
        monitor_memory
        ;;
    "heap-dump")
        create_heap_dump
        ;;
    "check")
        check_memory_threshold
        ;;
    *)
        echo "Usage: $0 {monitor|heap-dump|check}"
        echo "  monitor   - Continuous memory monitoring"
        echo "  heap-dump - Create JVM heap dump"
        echo "  check     - One-time memory threshold check"
        ;;
esac
