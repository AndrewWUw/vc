.class public if
.super java/lang/Object
	
	
	; standard class static initializer 
.method static <clinit>()V
	
	
	; set limits used by this method
.limit locals 0
.limit stack 0
	return
.end method
	
	; standard constructor initializer 
.method public <init>()V
.limit stack 1
.limit locals 1
	aload_0
	invokespecial java/lang/Object/<init>()V
	return
.end method
.method public static main([Ljava/lang/String;)V
L0:
.var 0 is argv [Ljava/lang/String; from L0 to L1
.var 1 is vc$ Lif; from L0 to L1
	new if
	dup
	invokenonvirtual if/<init>()V
	astore_1
	iconst_1
	ifeq L2
L2:
	iconst_1
	iconst_1
	if_icmpeq L3
	iconst_0
	goto L4
L3:
	iconst_1
L4:
	ifeq L5
	goto L6
L5:
L6:
L1:
	return
	
	; set limits used by this method
.limit locals 2
.limit stack 2
.end method
