.class public func
.super java/lang/Object
	
.field static k I
	
	; standard class static initializer 
.method static <clinit>()V
	
	iconst_3
	putstatic func/k I
	
	; set limits used by this method
.limit locals 0
.limit stack 1
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
.method a()I
L0:
.var 0 is this Lfunc; from L0 to L1
	iconst_4
	dup
	putstatic func/k I
	ireturn
L1:
	nop
	
	; set limits used by this method
.limit locals 1
.limit stack 3
.end method
.method public static main([Ljava/lang/String;)V
L0:
.var 0 is argv [Ljava/lang/String; from L0 to L1
.var 1 is vc$ Lfunc; from L0 to L1
	new func
	dup
	invokenonvirtual func/<init>()V
	astore_1
.var 2 is i I from L0 to L1
.var 3 is j [I from L0 to L1
	iconst_3
	newarray int
	dup
	iconst_0
	iconst_1
	iastore
	dup
	iconst_1
	iconst_2
	iastore
	dup
	iconst_2
	iconst_3
	iastore
	astore_3
	iconst_0
	dup
	istore_2
	invokestatic VC/lang/System/putIntLn(I)V
	aload_3
	iconst_1
	iconst_0
	dup_x2
	iastore
	invokestatic VC/lang/System/putIntLn(I)V
	getstatic func/k I
	invokestatic VC/lang/System/putIntLn(I)V
	aload_1
	invokevirtual func/a()I
	invokestatic VC/lang/System/putIntLn(I)V
L1:
	return
	
	; set limits used by this method
.limit locals 4
.limit stack 8
.end method
