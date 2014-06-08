.class public add
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
.var 1 is vc$ Ladd; from L0 to L1
	new add
	dup
	invokenonvirtual add/<init>()V
	astore_1
.var 2 is a I from L0 to L1
.var 3 is b I from L0 to L1
.var 4 is c I from L0 to L1
	iload_2
	invokestatic VC/lang/System.getInt()I
	istore_2
	pop
	iload_3
	invokestatic VC/lang/System.getInt()I
	istore_3
	pop
	iload 4
	iload_2
	iload_3
	iadd
	istore 4
	pop
	ldc "Sum: "
	invokestatic VC/lang/System/putString(Ljava/lang/String;)V
	iload 4
	invokestatic VC/lang/System.putInt(I)V
	invokestatic VC/lang/System/putLn()V
L1:
	return
	
	; set limits used by this method
.limit locals 5
.limit stack 5
.end method
