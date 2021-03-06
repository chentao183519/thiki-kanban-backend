#!/usr/bin/env bash
artifact=$1
server_host=$2
server_dir=$3

echo "-> upload artifact to remote server"
scp $artifact $server_host:$server_dir
scp killServer.sh $server_host:$server_dir

echo "-> login remote server"
ssh -t -t  $server_host dir_to_deploy=$server_dir 'bash -s'  <<'ENDSSH'

cd $dir_to_deploy
ls

echo "-> stop server"
sh killServer.sh

echo "-> start server"
nohup java -jar kanban-1.0-SNAPSHOT.jar &
echo "-> everything is done."

exit
ENDSSH
