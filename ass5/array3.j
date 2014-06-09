.class public array3
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
.var 1 is vc$ Larray3; from L0 to L1
	new array3
	dup
	invokenonvirtual array3/<init>()V
	astore_1
.var 2 is b [I from L0 to L1
	iconst_3
	newarray int
	dup
	iconst_0
	iconst_2
	iastore
	dup
	iconst_1
	iconst_4
	iastore
	dup
	iconst_2
	bipush 6
	iastore
	astore_2
.var 3 is c I from L0 to L1
	iload_3
	aload_2
	iconst_2
	bipush 8
	dup_x2
	iastore
	istore_3
	pop
	aload_2
	iconst_2
	iaload
	invokestatic VC/lang/System/putIntLn(I)V
L1:
	return
	
	; set limits used by this method
.limit locals 4
.limit stack 8
.end method
