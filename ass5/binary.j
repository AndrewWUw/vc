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
	ifeq L2
	iconst_0
	goto L3
L2:
	iconst_1
L3:
	pop
	iconst_0
	ifeq L4
	iconst_0
	ifeq L4
	iconst_1
	goto L5
L4:
	iconst_0
L5:
	pop
	iconst_0
	iconst_0
	if_icmpeq L8
	iconst_0
	goto L9
L8:
	iconst_1
L9:
	pop
	iconst_0
	iconst_0
	if_icmpne L12
	iconst_0
	goto L13
L12:
	iconst_1
L13:
	pop
	iconst_1
	iconst_1
	if_icmpeq L16
	iconst_0
	goto L17
L16:
	iconst_1
L17:
	pop
	iconst_1
	iconst_1
	if_icmpne L20
	iconst_0
	goto L21
L20:
	iconst_1
L21:
	pop
	iconst_1
	iconst_1
	if_icmplt L24
	iconst_0
	goto L25
L24:
	iconst_1
L25:
	pop
	iconst_1
	iconst_1
	if_icmple L28
	iconst_0
	goto L29
L28:
	iconst_1
L29:
	pop
	iconst_1
	iconst_1
	if_icmpgt L32
	iconst_0
	goto L33
L32:
	iconst_1
L33:
	pop
	iconst_1
	iconst_1
	if_icmpge L36
	iconst_0
	goto L37
L36:
	iconst_1
L37:
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
	ifeq L48
	iconst_0
	goto L49
L48:
	iconst_1
L49:
	pop
	fconst_1
	fconst_1
	fcmpg
	ifne L52
	iconst_0
	goto L53
L52:
	iconst_1
L53:
	pop
	fconst_1
	fconst_1
	fcmpg
	iflt L56
	iconst_0
	goto L57
L56:
	iconst_1
L57:
	pop
	fconst_1
	fconst_1
	fcmpg
	ifle L60
	iconst_0
	goto L61
L60:
	iconst_1
L61:
	pop
	fconst_1
	fconst_1
	fcmpg
	ifgt L64
	iconst_0
	goto L65
L64:
	iconst_1
L65:
	pop
	fconst_1
	fconst_1
	fcmpg
	ifge L68
	iconst_0
	goto L69
L68:
	iconst_1
L69:
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
	ifeq L80
	iconst_0
	goto L81
L80:
	iconst_1
L81:
	pop
	fconst_1
	iconst_1
	i2f
	fcmpg
	ifne L84
	iconst_0
	goto L85
L84:
	iconst_1
L85:
	pop
	iconst_1
	i2f
	fconst_1
	fcmpg
	ifge L88
	iconst_0
	goto L89
L88:
	iconst_1
L89:
	pop
	fconst_1
	iconst_1
	i2f
	fcmpg
	iflt L92
	iconst_0
	goto L93
L92:
	iconst_1
L93:
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
	if_icmpeq L110
	iconst_0
	goto L111
L110:
	iconst_1
L111:
	iconst_1
	if_icmpne L112
	iconst_0
	goto L113
L112:
	iconst_1
L113:
	pop
L1:
	return
	
	; set limits used by this method
.limit locals 2
.limit stack 3
.end method
