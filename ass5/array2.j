.class public array2
.super java/lang/Object
	
.field static a V
	
	; standard class static initializer 
.method static <clinit>()V
	
	iconst_3
	newarray int
	dup
	iconst_0
	iconst_1
	iastore
	dup
	iconst_1
	iconst_3
	iastore
	dup
	iconst_2
	iconst_5
	iastore
	putstatic array2/a [I
	
	; set limits used by this method
.limit locals 0
.limit stack 4
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
.var 1 is vc$ Larray2; from L0 to L1
	new array2
	dup
	invokenonvirtual array2/<init>()V
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
	getstatic array2/a [I
	iconst_2
	iaload
	invokestatic VC/lang/System/putIntLn(I)V
	aload_2
	iconst_2
	iaload
	invokestatic VC/lang/System/putIntLn(I)V
L1:
	return
	
	; set limits used by this method
.limit locals 3
.limit stack 7
.end method
