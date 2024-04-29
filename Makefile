# @file Makefile
# @brief Makefile for Assignment 7
# 
# @author Jamison Grudem
# @grace_days Using 2 grace days

# Predefined Variables: used for compilation and running
JC = javac
JR = java
BDIR = build
# Local bootstrap node
NODE = //localhost:8100/Node00
# Remote bootstrap node
RNODE = //csel-kh1250-10.cselabs.umn.edu:8110/Node00

# Optional Arguments
id?=00# The id of the server
loc?=local# The location of the server

# Compile all dependencies
# 	Ex1: "make"
# 	Ex2: "make all"
all: 
	-make clean
	mkdir log
	${JC} src/*.java -d ${BDIR}
	clear

# Run the client
#	Ex: "make client"
client:
	cd ${BDIR} && ${JR} src/Client ${NODE}
# Run the remote client
#	Ex: "make rclient"
rclient:
	cd ${BDIR} && ${JR} src/Client ${RNODE}

# Run the dictionary loader
#	Ex: "make dict"
dict:
	cd ${BDIR} && ${JR} src/DictionaryLoader ${NODE} ../dict.txt
# Run the remote dictionary loader
#	Ex: "make rdict"
rdict:
	cd ${BDIR} && ${JR} src/DictionaryLoader ${RNODE} ../dict.txt

# Run the entire DHT - all on a single host
#	Ex: "make dht"
dht:
	for i in {0..7}; do sleep 1; make node id=0$$i & done

# Run a single node 
# Default config: 00=bootstrap node, 01-08=chord nodes
# Parameters: id=node id, loc=local/remote
#	Ex: "make node"
#   Ex: "make node id=05"
node:
	clear
	cd ${BDIR} && ${JR} src/Node ${id} ../config/${loc}.txt

# Clean all build files and logs
# 	Ex: "make clean"
clean:
	@echo "Cleaning All Build & Log Files"
	-rm -rf ${BDIR}
	-rm -rf log