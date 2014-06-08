.class public ncr
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
.method factorial(I)I
L0:
.var 0 is this Lncr; from L0 to L1
.var 1 is n I from L0 to L1
.var 2 is c I from L0 to L1
.var 3 is result I from L0 to L1
	iconst_1
	istore_3
	iconst_1
	dup
	istore_2
	pop
L2:
	iload_2
	iload_1
	if_icmple L4
	iconst_0
	goto L5
L4:
	iconst_1
L5:
	ifeq L3
	iload_3
	iload_2
	imul
	dup
	istore_3
	pop
	iload_2
	iconst_1
	iadd
	dup
	istore_2
	pop
	goto L2
L3:
	iload_3
	ireturn
L1:
	nop
	
	; set limits used by this method
.limit locals 4
.limit stack 2
.end method
.method find_ncr(II)I
L0:
.var 0 is this Lncr; from L0 to L1
.var 1 is n I from L0 to L1
.var 2 is r I from L0 to L1
.var 3 is result I from L0 to L1
	aload_0
	iload_1
	invokevirtual ncr/factorial(I)I
	aload_0
	iload_2
	invokevirtual ncr/factorial(I)I
	aload_0
	iload_1
	iload_2
	isub
	invokevirtual ncr/factorial(I)I
	imul
	idiv
	dup
	istore_3
	pop
	iload_3
	ireturn
L1:
	nop
	
	; set limits used by this method
.limit locals 4
.limit stack 5
.end method
.method find_npr(II)I
L0:
.var 0 is this Lncr; from L0 to L1
.var 1 is n I from L0 to L1
.var 2 is r I from L0 to L1
.var 3 is result I from L0 to L1
	aload_0
	iload_1
	invokevirtual ncr/factorial(I)I
	aload_0
	iload_1
	iload_2
	isub
	invokevirtual ncr/factorial(I)I
	idiv
	dup
	istore_3
	pop
	iload_3
	ireturn
L1:
	nop
	
	; set limits used by this method
.limit locals 4
.limit stack 4
.end method
.method public static main([Ljava/lang/String;)V
L0:
.var 0 is argv [Ljava/lang/String; from L0 to L1
.var 1 is vc$ Lncr; from L0 to L1
	new ncr
	dup
	invokenonvirtual ncr/<init>()V
	astore_1
.var 2 is n I from L0 to L1
.var 3 is r I from L0 to L1
.var 4 is ncr I from L0 to L1
.var 5 is npr I from L0 to L1
	ldc "Enter the value of n and r
"
	invokestatic VC/lang/System/putString(Ljava/lang/String;)V
	invokestatic VC/lang/System.getInt()I
	dup
	istore_2
	pop
	invokestatic VC/lang/System.getInt()I
	dup
	istore_3
	pop
	aload_1
	iload_2
	iload_3
	invokevirtual ncr/find_ncr(II)I
	dup
	istore 4
	pop
	aload_1
	iload_2
	iload_3
	invokevirtual ncr/find_npr(II)I
	dup
	istore 5
	pop
	iload_2
	invokestatic VC/lang/System.putInt(I)V
	ldc "C"
	invokestatic VC/lang/System/putString(Ljava/lang/String;)V
	iload_3
	invokestatic VC/lang/System.putInt(I)V
	ldc " = "
	invokestatic VC/lang/System/putString(Ljava/lang/String;)V
	iload 4
	invokestatic VC/lang/System/putIntLn(I)V
	iload_2
	invokestatic VC/lang/System.putInt(I)V
	ldc "P"
	invokestatic VC/lang/System/putString(Ljava/lang/String;)V
	iload_3
	invokestatic VC/lang/System.putInt(I)V
	ldc " = "
	invokestatic VC/lang/System/putString(Ljava/lang/String;)V
	iload 5
	invokestatic VC/lang/System/putIntLn(I)V
	return
L1:
	return
	
	; set limits used by this method
.limit locals 6
.limit stack 3
.end method
