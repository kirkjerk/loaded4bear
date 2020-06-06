;BCD math routines
;9 Sept 2007 Curtis F Kaylor

;to add two BCD numbers, use
; a=addbcd(c,d)

addbcd
 sty temp1  ;store parameter 2
 sed        ;decimal mode on
 clc        ;no carry
 adc temp1  ;add to parameter 1
 cld        ;decimal mode off
 rts        ;accumulator is returned

subbcd
 sty temp1  ;store parameter 2
 sed        ;decimal mode on
 sec        ;no carry
 sbc temp1  ;subtract from parameter 1
 cld        ;decimal mode off
 rts        ;accumulator is returned
