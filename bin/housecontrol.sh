cd ..
rm nohup.out
mv log/app.log log/app.old

for port in `find /dev -name 'tty*'`
do
  PORTS="$PORTS:$port"
done
JAVA_OPT="-Djava.library.path=/usr/lib/jni -Dgnu.io.rxtx.SerialPorts=$PORTS"
nohup java $JAVA_OPT -cp "/usr/share/java/RXTXcomm.jar:lib/*:bin" net.geekgrandad.HouseControl $1 &
[ "$#" -eq "2" ] && echo $! >$2
cd bin
