.class public binary
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
.var 1 is vc$ Lbinary; from L0 to L1
	new binary
	dup
	invokenonvirtual binary/<init>()V
	astore_1
	iconst_1
	ifeq L2
	iconst_1
	goto L4
L2:
	iconst_1
	ifeq L3
	iconst_1
	goto L4
L3:
	iconst_0
L4:
	pop
	iconst_0
	ifeq L5
	iconst_0
	ifeq L5
	iconst_1
	goto L6
L5:
	iconst_0
L6:
	pop
	iconst_0
	iconst_0
	if_icmpeq L7
	iconst_0
	goto L8
L7:
	iconst_1
L8:
	pop
	iconst_0
	iconst_0
	if_icmpne L9
	iconst_0
	goto L10
L9:
	iconst_1
L10:
	pop
	iconst_1
	iconst_1
	if_icmpeq L11
	iconst_0
	goto L12
L11:
	iconst_1
L12:
	pop
	iconst_1
	iconst_1
	if_icmpne L13
	iconst_0
	goto L14
L13:
	iconst_1
L14:
	pop
	iconst_1
	iconst_1
	if_icmplt L15
	iconst_0
	goto L16
L15:
	iconst_1
L16:
	pop
	iconst_1
	iconst_1
	if_icmple L17
	iconst_0
	goto L18
L17:
	iconst_1
L18:
	pop
	iconst_1
	iconst_1
	if_icmpgt L19
	iconst_0
	goto L20
L19:
	iconst_1
L20:
	pop
	iconst_1
	iconst_1
	if_icmpge L21
	iconst_0
	goto L22
L21:
	iconst_1
L22:
	pop
	iconst_1
	iconst_1
	iadd
	pop
	iconst_1
	iconst_1
	isub
	pop
	iconst_1
	iconst_1
	imul
	pop
	iconst_1
	iconst_1
	idiv
	pop
	fconst_1
	fconst_1
	fcmpg
	ifeq L23
	iconst_0
	goto L24
L23:
	iconst_1
L24:
	pop
	fconst_1
	fconst_1
	fcmpg
	ifne L25
	iconst_0
	goto L26
L25:
	iconst_1
L26:
	pop
	fconst_1
	fconst_1
	fcmpg
	iflt L27
	iconst_0
	goto L28
L27:
	iconst_1
L28:
	pop
	fconst_1
	fconst_1
	fcmpg
	ifle L29
	iconst_0
	goto L30
L29:
	iconst_1
L30:
	pop
	fconst_1
	fconst_1
	fcmpg
	ifgt L31
	iconst_0
	goto L32
L31:
	iconst_1
L32:
	pop
	fconst_1
	fconst_1
	fcmpg
	ifge L33
	iconst_0
	goto L34
L33:
	iconst_1
L34:
	pop
	fconst_1
	fconst_1
	fadd
	pop
	fconst_1
	fconst_1
	fsub
	pop
	fconst_1
	fconst_1
	fmul
	pop
	fconst_1
	fconst_1
	fdiv
	pop
	iconst_1
	i2f
	fconst_1
	fcmpg
	ifeq L35
	iconst_0
	goto L36
L35:
	iconst_1
L36:
	pop
	fconst_1
	iconst_1
	i2f
	fcmpg
	ifne L37
	iconst_0
	goto L38
L37:
	iconst_1
L38:
	pop
	iconst_1
	i2f
	fconst_1
	fcmpg
	ifge L39
	iconst_0
	goto L40
L39:
	iconst_1
L40:
	pop
	fconst_1
	iconst_1
	i2f
	fcmpg
	iflt L41
	iconst_0
	goto L42
L41:
	iconst_1
L42:
	pop
	iconst_1
	i2f
	fconst_1
	fadd
	pop
	fconst_1
	iconst_1
	i2f
	fdiv
	pop
	iconst_1
	iconst_1
	iadd
	iconst_1
	iconst_1
	imul
	isub
	iconst_1
	iconst_1
	idiv
	if_icmpeq L43
	iconst_0
	goto L44
L43:
	iconst_1
L44:
	iconst_1
	if_icmpne L45
	iconst_0
	goto L46
L45:
	iconst_1
L46:
	pop
L1:
	return
	
	; set limits used by this method
.limit locals 2
.limit stack 3
.end method
