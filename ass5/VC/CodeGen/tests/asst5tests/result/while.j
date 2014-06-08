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
	ifeq L3
L4:
	goto L2
L5:
	goto L2
L3:
L6:
	iconst_1
	iconst_1
	if_icmpeq L8
	iconst_0
	goto L9
L8:
	iconst_1
L9:
	ifeq L7
L10:
	goto L7
L11:
	goto L6
L7:
L12:
	iconst_0
	ifeq L14
	iconst_1
	goto L16
L14:
	iconst_1
	ifeq L15
	iconst_1
	goto L16
L15:
	iconst_0
L16:
	ifeq L13
L17:
L19:
	iconst_3
	iconst_2
	if_icmpgt L21
	iconst_0
	goto L22
L21:
	iconst_1
L22:
	ifeq L20
L23:
	iconst_1
	ifeq L25
L27:
	goto L20
L28:
	goto L26
L25:
L29:
	goto L19
L30:
L26:
L24:
	goto L19
L20:
L18:
	goto L12
L13:
L1:
	return
	
	; set limits used by this method
.limit locals 2
.limit stack 2
.end method
