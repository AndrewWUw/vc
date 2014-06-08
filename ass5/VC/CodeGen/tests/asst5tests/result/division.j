.class public division
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
.var 1 is vc$ Ldivision; from L0 to L1
	new division
	dup
	invokenonvirtual division/<init>()V
	astore_1
.var 2 is first I from L0 to L1
.var 3 is second I from L0 to L1
.var 4 is firstf F from L0 to L1
	iconst_0
	i2f
	fstore 4
.var 5 is div F from L0 to L1
	ldc "Enter two integers"
	invokestatic VC/lang/System/putStringLn(Ljava/lang/String;)V
	invokestatic VC/lang/System.getInt()I
	dup
	istore_2
	pop
	iload_2
	i2f
	dup
	fstore 4
	pop
	invokestatic VC/lang/System.getInt()I
	dup
	istore_3
	pop
	iload_2
	iload_3
	idiv
	i2f
	dup
	fstore 5
	pop
	fload 5
	invokestatic VC/lang/System/putFloatLn(F)V
	fload 4
	iload_3
	i2f
	fdiv
	dup
	fstore 5
	pop
	fload 5
	invokestatic VC/lang/System/putFloatLn(F)V
L1:
	return
	
	; set limits used by this method
.limit locals 6
.limit stack 2
.end method
