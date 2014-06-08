.class public fib
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
.var 1 is vc$ Lfib; from L0 to L1
	new fib
	dup
	invokenonvirtual fib/<init>()V
	astore_1
.var 2 is n I from L0 to L1
	iconst_5
	istore_2
.var 3 is first I from L0 to L1
	iconst_0
	istore_3
.var 4 is second I from L0 to L1
	iconst_1
	istore 4
.var 5 is next I from L0 to L1
.var 6 is c I from L0 to L1
	iconst_0
	dup
	istore 6
	pop
L2:
	iload 6
	iload_2
	if_icmplt L4
	iconst_0
	goto L5
L4:
	iconst_1
L5:
	ifeq L3
L6:
	iload 6
	iconst_1
	if_icmple L8
	iconst_0
	goto L9
L8:
	iconst_1
L9:
	ifeq L10
	iload 6
	dup
	istore 5
	pop
	goto L11
L10:
L12:
	iload_3
	iload 4
	iadd
	dup
	istore 5
	pop
	iload 4
	dup
	istore_3
	pop
	iload 5
	dup
	istore 4
	pop
L13:
L11:
	iload 5
	invokestatic VC/lang/System/putIntLn(I)V
L7:
	iload 6
	iconst_1
	iadd
	dup
	istore 6
	pop
	goto L2
L3:
	return
L1:
	return
	
	; set limits used by this method
.limit locals 7
.limit stack 2
.end method
