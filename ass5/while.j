.class public while
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
.var 1 is vc$ Lwhile; from L0 to L1
	new while
	dup
	invokenonvirtual while/<init>()V
	astore_1
L2:
	iconst_1
	iconst_1
	if_icmpeq L6
	iconst_0
	goto L7
L6:
	iconst_1
L7:
	ifeq L3
L8:
	ldc "a"
	invokestatic VC/lang/System/putString(Ljava/lang/String;)V
	goto L3
L9:
	goto L2
L3:
L10:
	iconst_0
	ifeq L12
	iconst_1
	ifeq L12
	iconst_0
	goto L13
L12:
	iconst_1
L13:
	ifeq L11
L14:
L16:
	iconst_3
	iconst_2
	if_icmpgt L20
	iconst_0
	goto L21
L20:
	iconst_1
L21:
	ifeq L17
L22:
	iconst_1
	ifeq L24
L25:
	goto L17
L26:
	goto L27
L24:
L28:
	goto L16
L29:
L27:
L23:
	goto L16
L17:
L15:
	goto L10
L11:
L1:
	return
	
	; set limits used by this method
.limit locals 2
.limit stack 2
.end method
