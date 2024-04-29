# Predefined Variables: used for compilation and running
JC = javac
JR = java
BDIR = build
NODE = //localhost:8100/Node00

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

# Run the dictionary loader
#	Ex: "make dict"
dict:
	cd ${BDIR} && ${JR} src/DictionaryLoader ${NODE} ../dict.txt

# Run a single node 
# Default config: 00=bootstrap node, 01-08=chord nodes
# Parameters: id=node id, s=seconds to run
#	Ex: "make node"
#   Ex: "make node id=05"
#   Ex: "make node id=08 s=60"
node:
	clear
	cd ${BDIR} && ${JR} src/Node ${id} ../config/${loc}.txt

# Clean all build files and logs
# 	Ex: "make clean"
clean:
	@echo "Cleaning All Build & Log Files"
	-rm -rf ${BDIR}
	-rm -rf log