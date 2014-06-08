.class public initexpr
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
.method ifunc()I
L0:
.var 0 is this Linitexpr; from L0 to L1
	iconst_1
	ireturn
L1:
	nop
	
	; set limits used by this method
.limit locals 1
.limit stack 1
.end method
.method ffunc()F
L0:
.var 0 is this Linitexpr; from L0 to L1
	iconst_1
	i2f
	freturn
L1:
	nop
	
	; set limits used by this method
.limit locals 1
.limit stack 1
.end method
.method public static main([Ljava/lang/String;)V
L0:
.var 0 is argv [Ljava/lang/String; from L0 to L1
.var 1 is vc$ Linitexpr; from L0 to L1
	new initexpr
	dup
	invokenonvirtual initexpr/<init>()V
	astore_1
.var 2 is i I from L0 to L1
.var 3 is f F from L0 to L1
.var 4 is b Z from L0 to L1
.var 5 is fa [F from L0 to L1
	iconst_3
	newarray float
	astore 5
.var 6 is ia [I from L0 to L1
	iconst_3
	newarray int
	astore 6
.var 7 is x1 [I from L0 to L1
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
	astore 7
.var 8 is x2 [I from L0 to L1
	iconst_3
	newarray int
	dup
	iconst_0
	iconst_1
	iastore
	dup
	iconst_1
	iload_2
	iastore
	dup
	iconst_2
	iconst_3
	iastore
	astore 8
.var 9 is x5 [F from L0 to L1
	iconst_3
	newarray float
	dup
	iconst_0
	iconst_1
	i2f
	fastore
	dup
	iconst_1
	iconst_2
	i2f
	fastore
	dup
	iconst_2
	iconst_3
	i2f
	fastore
	astore 9
.var 10 is x6 [I from L0 to L1
	iconst_3
	newarray int
	dup
	iconst_0
	iconst_3
	dup
	istore_2
	iastore
	dup
	iconst_1
	iload_2
	iload_2
	imul
	iload_2
	iload_2
	idiv
	iadd
	iastore
	dup
	iconst_2
	iload_2
	iconst_1
	isub
	iastore
	astore 10
.var 11 is x7 [F from L0 to L1
	iconst_4
	newarray float
	dup
	iconst_0
	fconst_1
	fastore
	dup
	iconst_1
	iconst_2
	i2f
	ldc 3.0
	fadd
	fastore
	dup
	iconst_2
	iload_2
	i2f
	fconst_1
	fdiv
	fastore
	dup
	iconst_3
	sipush 1234
	i2f
	fastore
	astore 11
.var 12 is x9 [F from L0 to L1
	iconst_3
	newarray float
	dup
	iconst_0
	iconst_1
	i2f
	fastore
	dup
	iconst_1
	iconst_3
	dup
	istore_2
	i2f
	fastore
	dup
	iconst_2
	iconst_3
	i2f
	fastore
	astore 12
.var 13 is x92 [F from L0 to L1
	iconst_2
	newarray float
	dup
	iconst_0
	iconst_1
	i2f
	fastore
	dup
	iconst_1
	aload 5
	iconst_1
	faload
	fastore
	astore 13
.var 14 is x10 [F from L0 to L1
	iconst_1
	newarray float
	dup
	iconst_0
	aload 14
	iconst_0
	faload
	fastore
	astore 14
.var 15 is x11 [Z from L0 to L1
	iconst_1
	newarray boolean
	dup
	iconst_0
	aload 15
	iconst_0
	baload
	ifeq L4
	iconst_0
	goto L5
L4:
	iconst_1
L5:
	ifeq L2
	aload 6
	iconst_1
	iaload
	i2f
	aload 5
	iconst_1
	faload
	fcmpg
	ifeq L6
	iconst_0
	goto L7
L6:
	iconst_1
L7:
	ifeq L2
	iconst_1
	goto L3
L2:
	iconst_0
L3:
	bastore
	astore 15
.var 16 is x12 [F from L0 to L1
	iconst_1
	newarray float
	dup
	iconst_0
	aload 16
	iconst_0
	iconst_3
	iconst_3
	imul
	i2f
	aload 16
	iconst_0
	faload
	fadd
	dup_x2
	fastore
	fastore
	astore 16
.var 17 is x14 [I from L0 to L1
	iconst_2
	newarray int
	dup
	iconst_0
	iconst_1
	iastore
	dup
	iconst_1
	aload_1
	invokevirtual initexpr/ifunc()I
	iastore
	astore 17
.var 18 is x16 [F from L0 to L1
	iconst_2
	newarray float
	dup
	iconst_0
	iconst_1
	i2f
	fastore
	dup
	iconst_1
	aload_1
	invokevirtual initexpr/ifunc()I
	i2f
	fastore
	astore 18
.var 19 is x17 [F from L0 to L1
	iconst_2
	newarray float
	dup
	iconst_0
	iconst_1
	i2f
	fastore
	dup
	iconst_1
	aload_1
	invokevirtual initexpr/ffunc()F
	fastore
	astore 19
L1:
	return
	
	; set limits used by this method
.limit locals 20
.limit stack 8
.end method
