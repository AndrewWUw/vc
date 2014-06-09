.class public initexpr2
.super java/lang/Object
	
.field static i I
.field static f F
.field static b Z
.field static fa [F
.field static ia [I
.field static x1 [I
.field static x2 [I
.field static x5 [F
.field static x6 [I
.field static x7 [F
.field static x9 [F
.field static x92 [F
.field static x14 [I
.field static x16 [F
.field static x17 [F
	
	; standard class static initializer 
.method static <clinit>()V
	
	iconst_0
	putstatic initexpr2/i I
	fconst_0
	putstatic initexpr2/f F
	iconst_0
	putstatic initexpr2/b Z
	iconst_3
	newarray float
	putstatic initexpr2/fa [F
	iconst_3
	newarray int
	putstatic initexpr2/ia [I
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
	putstatic initexpr2/x1 [I
	iconst_3
	newarray int
	dup
	iconst_0
	iconst_1
	iastore
	dup
	iconst_1
	getstatic initexpr2/i I
	iastore
	dup
	iconst_2
	iconst_3
	iastore
	putstatic initexpr2/x2 [I
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
	putstatic initexpr2/x5 [F
	iconst_3
	newarray int
	dup
	iconst_0
	iconst_3
	dup
	putstatic initexpr2/i I
	iastore
	dup
	iconst_1
	getstatic initexpr2/i I
	getstatic initexpr2/i I
	imul
	getstatic initexpr2/i I
	getstatic initexpr2/i I
	idiv
	iadd
	iastore
	dup
	iconst_2
	getstatic initexpr2/i I
	iconst_1
	isub
	iastore
	putstatic initexpr2/x6 [I
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
	getstatic initexpr2/i I
	i2f
	fconst_1
	fdiv
	fastore
	dup
	iconst_3
	sipush 1234
	i2f
	fastore
	putstatic initexpr2/x7 [F
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
	putstatic initexpr2/i I
	i2f
	fastore
	dup
	iconst_2
	iconst_3
	i2f
	fastore
	putstatic initexpr2/x9 [F
	iconst_2
	newarray float
	dup
	iconst_0
	iconst_1
	i2f
	fastore
	dup
	iconst_1
	getstatic initexpr2/fa [F
	iconst_1
	faload
	fastore
	putstatic initexpr2/x92 [F
	iconst_2
	newarray int
	dup
	iconst_0
	iconst_1
	iastore
	dup
	iconst_1
	aload_0
	invokevirtual initexpr2/ifunc()I
	iastore
	putstatic initexpr2/x14 [I
	iconst_2
	newarray float
	dup
	iconst_0
	iconst_1
	i2f
	fastore
	dup
	iconst_1
	aload_0
	invokevirtual initexpr2/ifunc()I
	i2f
	fastore
	putstatic initexpr2/x16 [F
	iconst_2
	newarray float
	dup
	iconst_0
	iconst_1
	i2f
	fastore
	dup
	iconst_1
	aload_0
	invokevirtual initexpr2/ffunc()F
	fastore
	putstatic initexpr2/x17 [F
	
	; set limits used by this method
.limit locals 0
.limit stack 41
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
.var 0 is this Linitexpr2; from L0 to L1
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
.var 0 is this Linitexpr2; from L0 to L1
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
.var 1 is vc$ Linitexpr2; from L0 to L1
	new initexpr2
	dup
	invokenonvirtual initexpr2/<init>()V
	astore_1
	return
L1:
	return
	
	; set limits used by this method
.limit locals 2
.limit stack 2
.end method
