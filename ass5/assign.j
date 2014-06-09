.class public assign
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
.var 1 is vc$ Lassign; from L0 to L1
	new assign
	dup
	invokenonvirtual assign/<init>()V
	astore_1
.var 2 is i I from L0 to L1
	iconst_1
	istore_2
.var 3 is f F from L0 to L1
	fconst_1
	fstore_3
	iconst_2
	istore_2
	iconst_3
	dup
	istore_2
	istore_2
	fconst_2
	fstore_3
	ldc 3.0
	dup
	fstore_3
	fstore_3
	iload_2
	istore_2
	iload_2
	dup
	istore_2
	istore_2
L1:
	return
	
	; set limits used by this method
.limit locals 4
.limit stack 16
.end method
