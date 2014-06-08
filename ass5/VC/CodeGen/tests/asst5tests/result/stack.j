.class public stack
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
.var 1 is vc$ Lstack; from L0 to L1
	new stack
	dup
	invokenonvirtual stack/<init>()V
	astore_1
.var 2 is i I from L0 to L1
	iconst_1
	dup
	istore_2
	pop
	iconst_1
	pop
	iconst_1
	iconst_1
	iadd
	pop
	iconst_1
	iconst_1
	iadd
	iconst_1
	iadd
	iconst_1
	iadd
	pop
L2:
	iconst_1
	ifeq L3
	iconst_1
	iconst_1
	iadd
	iconst_1
	iadd
	iconst_1
	iadd
	pop
	goto L2
L3:
L1:
	return
	
	; set limits used by this method
.limit locals 3
.limit stack 2
.end method
