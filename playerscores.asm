;PlayerScores MiniKernal
;9 Sept 2007 - Curtis F Kaylor
;displays two digit score for each of two players
;
;Copyright 2007 Curtis F Kaylor
;Permission is hereby granted to distribute this code with the bAtari Basic
;compiler, under the terms of the bAtari Basic license.

 ifnconst playerscores
playerscores = 2  ;default is two
 endif

player0score = lifepointer ; BCD
player1score =  lives; BCD
player0scorecolor = lifecolor
player1scorecolor = statusbarlength

 ifconst noscore
player2score = score ; BCD
player3score = score+1; BCD
player2scorecolor = score+2
player3scorecolor = scorecolor
 endif
 
;assign alternate names for easier coding
temp0digit1 = temp1
temp0digit2 = temp3
temp1digit1 = temp5
temp1digit2 = stack1
 if playerscores = 2           ;if using only one set of scores
temp0color = player0scorecolor ;I can reference the color 
temp1color = player1scorecolor ;variables directly   
 else                          ;otherwise
temp0color = stack3            ;I need to store the intermediate values
temp1color = stack4            ;using more precious stack space
 endif

 ;Usage: builddigits score digit1pointer digit2pointer  
 ;Builds digit pointers from BCD score value
 MAC builddigits
 ;firsy digit
 lda {1}           ;3 (3) get score
 and #$F0          ;2 (5) strip off low digit
 lsr               ;2 (7) divide by two = digit * 8
 adc <#scoretable  ;2 (9) add to base address 
 sta {2}           ;3 (12) store address low byte
 ;second digit
 lda {0}           ;3 (15) get score again
 and #$0F          ;2 (17) strip off high digit
 asl               ;2 (19) multiply by eight
 asl               ;2 (21)
 asl               ;2 (23)
 adc <#scoretable  ;2 (25) add to base address  
 sta {3}           ;3 (28) store address low byte
 ENDM

minikernel ;this is where the kernel JSRs

 ;only need to do this once - first lines
 ldx #>scoretable     ;2 (2) score graphics high byte
 stx temp0digit1+1  ;3 (5)
 stx temp0digit2+1  ;3 (8)
 stx temp1digit1+1  ;3 (11)
 stx temp1digit2+1  ;3 (14)
 
 lda player0scorecolor  ;3 (17) if playerscores = 2 this just loads the
 sta temp0color         ;3 (20) colors back into the original locations
 lda player1scorecolor  ;3 (23) otherwise it loads them into temporary
 sta temp1color         ;3 (26) variables
 
 clc ;2  for the ADCs coming up

 sta WSYNC  ;second line 
 builddigits player0score, temp0digit1, temp0digit2 ;28 (30)
 builddigits player1score, temp1digit1, temp1digit2 ;28 (58)
 jsr minikerneldisplay ;6 (64) 9 more lines! 

 if playerscores=4
 lda player2scorecolor  ;3 (+3) load colors into
 sta temp0color         ;3 (+6) temporary variables
 lda player3scorecolor  ;3 (+9) used by the
 sta temp1color         ;3 (+12) display routine
 clc                    ;2 (+14) for the ADCs coming up
 sta WSYNC              ;3 (+17) looks like there were enough cycles left
 builddigits player2score, temp0digit1, temp0digit2 ;28 (30)
 builddigits player3score, temp1digit1, temp1digit2 ;28 (58)
 jsr minikerneldisplay ;6 (64) 9 more lines! 
 endif ;playerscores
 
endminikernel
 sta WSYNC  ; one more scanline to finish things off
            ; Y is 0 coming out of minikerneldisplay  
 sty NUSIZ0 ;3 (3) Reset Player/Missile 0 size 
 sty NUSIZ1 ;3 (6) Reset Player/Missile 1 size
 
 sta WSYNC  ;score routine expects to be at the beginning of a line
 rts  ;return to the kernel

minikerneldisplay

 ;set up the player positions - uses up one line
 sta WSYNC  ;wait till the end of the line (count cycles)
 lda #$02  ;2 (2) two copies medium - normal sized
 sta NUSIZ0 ;3 (5)  
 sta NUSIZ1 ;3 (8) 
 lda temp0color  ;3 (11) not really necessary  
 sta COLUP0 ;3 (14) just using up cycles
 lda temp1color  ;3 (17) same with these two 
 sta COLUP1 ;3 (20) lines
 sleep 20 ;20 (40)
 sta RESP0  ;3 (43) Position Player0 
 sta RESP1  ;3 (46) Position Player1
 lda #$E0  ;2 (48) Move Two Clocks Right 
 sta HMP0 ;3 (51) Set Player 1
 lda #$00 ;2 (53) Turning Off Vertical Delay
 sta HMP1; 3 (56) No Movement
 sta VDELP0 ;3 (59) Displays Player0
 sta VDELP1 ;3 (62) and Player1
 sleep 2;2 (64)
 ldy #7 ;2 (66) 8 lines in the digits 
 sty temp7 ;3 (69)
 sta HMOVE ;3 (72) have to do during horizontal blank
 
minikernelloop
 ;this draws two scores - uses up 8 lines
 sta WSYNC  ;wait till line starts (count cycles)
 lda (temp0digit1),y ;5 (8) get the graphic
 sta GRP0 ;3 (11)
 ldx temp0color  ;3 (3) left player score color
 stx COLUP0 ;3 (14) goes in both digits
 lda (temp0digit2),y ;5 (19) get the graphic
 sta GRP1 ;3 (22)
 stx COLUP1 ;3 (25) 
 lda (temp1digit2),y ;5 (30) get graphic
 tax ;2 (32) store in X
 lda (temp1digit1),y ;5 (37) get the graphic
 ldy temp1color  ;3 (40) right player score color
 ldy temp1color  ;3 (43) need a 3 cycle delay...
 sty COLUP0 ;3 (45) player 0 color
 sta GRP0 ;3 (46) player 0 data
 sty COLUP1 ;3 (48) player 1 color
 stx GRP1 ;3 (51) player 1 data
 dec temp7  ;2 (53)
 ldy temp7  ;3  (56)
 bpl minikernelloop ;2+ (58-59)
 iny ;2 (60-61) make it 0
 sty GRP0 ;3 (63-64) clear player0
 sty GRP1 ;3 (66-67) clear player1
 rts ;6 (70-71) only 5  or 6 cycles left in the last line

