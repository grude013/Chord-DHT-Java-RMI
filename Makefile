# Predefined Variables: used for compilation and running
JC = javac
JR = java
BDIR = build

all: 
	-make clean
	mkdir log
	mkdir log/html
	${JC} src/*.java -d build
	echo "\n\n"

client:
	cd ${BDIR} && ${JR} src/Client

dict:
	cd ${BDIR} && ${JR} src/DictionaryLoader test ../dict.txt

node2:
	cd ${BDIR} && ${JR} src/BootstrapNode 01 ../config/local.txt
node:
	cd ${BDIR} && ( ${JR} src/BootstrapNode 01 ../config/local.txt & \
					${JR} src/ChordNode 02 ../config/local.txt & \
					${JR} src/ChordNode 03 ../config/local.txt & \
					${JR} src/ChordNode 04 ../config/local.txt & \
					${JR} src/ChordNode 05 ../config/local.txt & \
					${JR} src/ChordNode 06 ../config/local.txt & \
					${JR} src/ChordNode 07 ../config/local.txt & \
					${JR} src/ChordNode 08 ../config/local.txt & echo "")

b:
	cd ${BDIR} && ${JR} src/BootstrapNode 03 ../config/local.txt && cd ..
c1:
	cd ${BDIR} && ${JR} src/ChordNode 08 ../config/local.txt && cd ..
c2:
	cd ${BDIR} && ${JR} src/ChordNode 07 ../config/local.txt && cd ..

clean:
	rm -rf ${BDIR}
	rm -rf log

test: 
	make
	cd ${BDIR} && ${JR} src/Test && cd ..

range:
	make
	cd ${BDIR} && ${JR} src/Range && cd ..