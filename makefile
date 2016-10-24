JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
        $(JC) $(JFLAGS) $*.java

CLASSES = \
        src/common/Logger.java \
	src/common/Operation.java \
        src/interfaces/RPCInterf.java \
	src/client/TCPClient.java \
	src/client/UDPClient.java \
	src/client/RPCClient.java \
	src/client/Client.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
        $(RM) *.class