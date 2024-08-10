#/bin/bash

if [ "$#" -ne 1 ]; then
    echo "Uso: $0 {down|stop}"
    exit 1
fi

ACTION=$1

case $ACTION in
    down)
        echo "Executando docker-compose down"
        docker-compose -f app/docker-compose.yml down
        ;;
    stop)
        echo "Executando comando de stop"
        docker-compose -f app/docker-compose.yml stop
        ;;
    *)
        echo "Opção inválida: $ACTION"
        echo "Uso: $0 {down|stop}"
        exit 1
        ;;
esac