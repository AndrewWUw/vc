.class public t
.super java/lang/Object
	
.field static fs V
	
	; standard class static initializer 
.method static <clinit>()V
	
	fconst_0
	iconst_2
	newarray float
	putstatic t/fs [F
	
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
.method public static main([Ljava/lang/String;)V
L0:
.var 0 is argv [Ljava/lang/String; from L0 to L1
.var 1 is vc$ Lt; from L0 to L1
	new t
	dup
	invokenonvirtual t/<init>()V
	astore_1
	getstatic t/fs [F
	iconst_0
	iaload
	invokestatic VC/lang/System/putFloatLn(F)V
	getstatic t/fs [F
	iconst_1
	iaload
	invokestatic VC/lang/System/putFloatLn(F)V
	return
L1:
	return
	
	; set limits used by this method
.limit locals 2
.limit stack 3
.end method
