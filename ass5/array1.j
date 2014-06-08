.class public array1
.super java/lang/Object
	
.field static i1 V
	
	; standard class static initializer 
.method static <clinit>()V
	
	iconst_1
	newarray int
	putstatic array1/i1 [I
	
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
.method foo([I[F)V
	
	; return may not be present in a VC function returning void
	; The following return inserted by the VC compiler
	return
	
	; set limits used by this method
.limit locals 1
.limit stack 2
.end method
.method foo2([I[F)I
L0:
.var 0 is this Larray1; from L0 to L1
.var 1 is a [I from L0 to L1
.var 2 is f [F from L0 to L1
	iconst_1
	ireturn
L1:
	nop
	
	; set limits used by this method
.limit locals 3
.limit stack 3
.end method
.method public static main([Ljava/lang/String;)V
L0:
.var 0 is argv [Ljava/lang/String; from L0 to L1
.var 1 is vc$ Larray1; from L0 to L1
	new array1
	dup
	invokenonvirtual array1/<init>()V
	astore_1
.var 2 is i2 [I from L0 to L1
	iconst_2
	newarray int
	astore_2
.var 3 is f1 [F from L0 to L1
	iconst_2
	newarray float
	astore_3
	getstatic array1/i1 [I
	iconst_0
	iaload
	aload_2
	iconst_1
	iaload
	getstatic array1/i1 [I
	iconst_0
	aload_2
	iconst_0
	aload_2
	iconst_1
	iaload
	dup_x2
	iastore
	iastore
	pop
	aload_1
	aload_2
	aload_3
	invokevirtual array1/foo([I[F)V
	aload_1
	aload_2
	aload_3
	invokevirtual array1/foo2([I[F)I
	pop
	return
L1:
	return
	
	; set limits used by this method
.limit locals 4
.limit stack 12
.end method
