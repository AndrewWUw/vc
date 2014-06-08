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
L2:
L4:
	sipush 10000
	sipush 10000
	iadd
	pop
	goto L2
L5:
	goto L2
L3:
L6:
L8:
	sipush 20000
	sipush 20000
	iadd
	pop
	goto L7
L9:
	goto L6
L7:
	iconst_0
	dup
	istore_2
	pop
L10:
	iload_2
	iconst_3
	if_icmplt L12
	iconst_0
	goto L13
L12:
	iconst_1
L13:
	ifeq L11
L14:
	sipush 30000
	sipush 30000
	iadd
	pop
	goto L10
	goto L11
L15:
	iload_2
	iconst_1
	iadd
	dup
	istore_2
	pop
	goto L10
L11:
L16:
	goto L16
L17:
L1:
	return
	
	; set limits used by this method
.limit locals 3
.limit stack 2
.end method
