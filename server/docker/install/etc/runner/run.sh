trap "true" TERM INT

mkdir -p /var/log/supervisor
/usr/local/bin/supervisord -c /etc/supervisor/supervisord.conf &
SUPERVISORD_PID=$!

while supervisorctl status | grep -q -v "RUNNING\|STOPPED\|STOPPING"; do
    (kill -0 $SUPERVISORD_PID 2> /dev/null) || (echo "Supervisord died unexpectedly"; exit 1)
    sleep 5
done

if [[ -f "/run/secrets/SSL_KEY" ]]; then
    HTTPS_SUBSTR="-https"
fi

for ((i = 0; i < 4; i++)) do
    supervisorctl start "uvicorn$HTTPS_SUBSTR:uvicorn-$i"
done

wait $SUPERVISORD_PID || :
supervisorctl shutdown
wait $SUPERVISORD_PID || :
