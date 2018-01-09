JC = javac
JFLAGS = -g
OUTDIR = ./classes

.SUFFIXES: .java .class

.java.class:
	$(JC) -d $(OUTDIR) $(JFLAGS) src/*.java

default: .java.class

clean:
	$(RM) -r $(OUTDIR)/classes/