.class public fact
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
.var 1 is vc$ Lfact; from L0 to L1
	new fact
	dup
	invokenonvirtual fact/<init>()V
	astore_1
.var 2 is c I from L0 to L1
.var 3 is n I from L0 to L1
.var 4 is fact I from L0 to L1
	iconst_1
	istore 4
	ldc "Enter a number to calculate it's factorial
"
	invokestatic VC/lang/System/putString(Ljava/lang/String;)V
	invokestatic VC/lang/System.getInt()I
	istore_3
L2:
	iconst_1
	istore_2
	iload_2
	iload_3
	if_icmple L6
	iconst_0
	goto L7
L6:
	iconst_1
L7:
	ifeq L3
	iload 4
	iload_2
	imul
	istore 4
	iload_2
	iconst_1
	iadd
	istore_2
	goto L2
L3:
	ldc "Factorial of "
	invokestatic VC/lang/System/putString(Ljava/lang/String;)V
	iload_3
	invokestatic VC/lang/System.putInt(I)V
	ldc " = "
	invokestatic VC/lang/System/putString(Ljava/lang/String;)V
	iload 4
	invokestatic VC/lang/System/putIntLn(I)V
	return
L1:
	return
	
	; set limits used by this method
.limit locals 5
.limit stack 9
.end method
