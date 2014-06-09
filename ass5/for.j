.class public for
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
.var 1 is vc$ Lfor; from L0 to L1
	new for
	dup
	invokenonvirtual for/<init>()V
	astore_1
.var 2 is i I from L0 to L1
	bipush 100
	sipush 200
	iadd
	pop
L4:
L6:
	sipush 10000
	sipush 10000
	iadd
	pop
	goto L4
L7:
	goto L4
L5:
L10:
L12:
	sipush 20000
	sipush 20000
	iadd
	pop
	goto L11
L13:
	goto L10
L11:
L16:
	iconst_0
	istore_2
	iload_2
	iconst_3
	if_icmplt L20
	iconst_0
	goto L21
L20:
	iconst_1
L21:
	ifeq L17
L22:
	sipush 30000
	sipush 30000
	iadd
	pop
	goto L16
	goto L17
L23:
	iload_2
	iconst_1
	iadd
	istore_2
	goto L16
L17:
L28:
	goto L28
L29:
L1:
	return
	
	; set limits used by this method
.limit locals 3
.limit stack 6
.end method
