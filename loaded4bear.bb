
  dim musicPointer=a
  dim musicTimer=b
  dim beatPointer=c
  dim beatTimer=d

  dim bear0leftright = e ; 
  dim bear1leftright = f ; 
  
  dim BunchOfFlags = g;

  def bear0needsToRecharge=BunchOfFlags{0}
  def bear1needsToRecharge=BunchOfFlags{1}
  def game_is_finished = BunchOfFlags{2}

  def bullet0wasfiring = BunchOfFlags{3}
  def bullet1wasfiring = BunchOfFlags{4}

  def cpu_in_control = BunchOfFlags{5}
  def already_switched_cpu = BunchOfFlags{6}
  def already_switch_cpu_joystick = BunchOfFlags{7}

  const MODE_TITLE = 0
  const MODE_GAME = 1
  const MODE_END = 2
  
  dim winnerbearcolor = h;
  dim winningbeardir = i; 

  dim game_state_mode = j;

  dim just_a_timer = k

  dim bear0freaktimer = l
  dim bear1freaktimer = m

  dim walkingframe0 = n
  dim walkingframe1 = o

  dim otherbearjoystick = p
  def otherbear_j1fire = otherbearjoystick{0}
  def otherbear_j1up = otherbearjoystick{1}
  def otherbear_j1down = otherbearjoystick{2}
  def otherbear_j1left = otherbearjoystick{3}
  def otherbear_j1right = otherbearjoystick{4}


  dim cpu_mood_counter = q
  dim cpu_mood = r

  const CPU_CHASE = 1
  const CPU_FIRE = 2
  const CPU_RAMBLE = 3

  const font = whimsey

  const NORMAL_VOLUME = 8

  const TIME_BETWEEN_ENDING_SCROLL = 8

  const REFLECTLEFT = 8
  const REFLECTRIGHT = 0
  
  const BEAR_BROWN = 32
  const BEAR_BLUE = 112
  const TREE_GREEN = $D4
  
  const OFFSCREEN_POS = 200

  missile0height = 4
  missile1height = 4
  
  player0scorecolor = BEAR_BROWN
  player1scorecolor = BEAR_BLUE
  
  walkingframe0 = 0
  walkingframe1 = 0

  const PLAYERSINFRONT = 1
  const PLAYERSBEHIND = 5
  

  

   rem  *  Clears all normal variables.
   for temp5 = 0 to 25 : a[temp5] = 0 : next

  cpu_in_control = 1

 goto __init_titlescreen__

main

  if switchreset then reboot
  ;gosub _frame0_for_0_
  if walkingframe1 = 0 then gosub _frame0_for_0_
  if walkingframe1 = 10 then gosub _frame1_for_0_
  
  if walkingframe0 > 19 then walkingframe0 = 0

  walkingframe0 = walkingframe0 + 1


  if walkingframe1 = 0 then gosub _frame0_for_1_

  if walkingframe1 = 10 then gosub _frame1_for_1_
  
  
  walkingframe1 = walkingframe1 + 1
  
  if walkingframe1 > 19 then walkingframe1 = 0
  

  on game_state_mode goto _main_title_ _main_game_ _main_ending_

_main_title_

 if musicTimer = 0 then gosub changeMusicNoteTitle
 musicTimer = musicTimer - 1

 if beatTimer = 0 then gosub changeBeatNoteTitle
 beatTimer = beatTimer - 1

  rem already_switched_cpu = BunchOfFlags{6} 
  rem cpu_in_control = BunchOfFlags{5} 
  
  

  
  rem if !switchselect then already_switched_cpu = 0 else goto _done_handling_switch_down_
  if !switchselect then already_switched_cpu = 0 : goto _done_handling_switch_down_
  if already_switched_cpu then goto _done_handling_switch_down_
  
  BunchOfFlags{5} = !BunchOfFlags{5}
  already_switched_cpu = 1
_done_handling_switch_down_

  if !joy0right then already_switch_cpu_joystick = 0: goto _done_handling_title_joystick_
  if already_switch_cpu_joystick then goto _done_handling_title_joystick_

  /* if joy0left then BunchOfFlags{5} = !BunchOfFlags{5}: already_switch_cpu_joystick = 1; */
  if joy0right then BunchOfFlags{5} = !BunchOfFlags{5}: already_switch_cpu_joystick = 1;

_done_handling_title_joystick_
  player0x = 44 : player0y = 87
  player1x = 116 : player1y = 85

  /* gosub _frame0_for_0_ */
  if walkingframe1 = 0 then gosub _frame0_for_0_
  if walkingframe1 = 10 then gosub _frame1_for_0_
  if walkingframe0 > 19 then walkingframe0 = 0

  walkingframe0 = walkingframe0 + 1

  gosub _frame_ai_for_1_
  rem 
  rem serious weirdness, colors were messed up when I changed them before gosub to set player???
  
  if cpu_in_control then NUSIZ0 = $00 else NUSIZ0 = $01
  
  NUSIZ1 = $07
  
  COLUP0 = BEAR_BROWN
  COLUP1 = $DC
  REFP0 = REFLECTRIGHT
  REFP1 = REFLECTRIGHT

  playfieldpos = 8
  drawscreen
  
  if joy0fire || joy1fire then goto __init_game__
  
  
  goto main

_main_game_

  otherbearjoystick = 0

  if cpu_in_control goto _cpuBearControl

  if joy1fire then otherbear_j1fire = 1
  if joy1left then otherbear_j1left = 1
  if joy1right then otherbear_j1right = 1
  if joy1up then otherbear_j1up = 1
  if joy1down then otherbear_j1down = 1
  goto _endPlayer1Move
_cpuBearControl
  if cpu_mood = CPU_FIRE then otherbear_j1fire = 1
  if cpu_mood_counter <> 0 then goto _keepdoingwhatyourdoing
  rem cpu mood counter is at zero ,
  if cpu_mood = CPU_CHASE then cpu_mood = CPU_FIRE : goto _donechangemood
  if cpu_mood = CPU_FIRE then cpu_mood = CPU_CHASE : goto _donechangemood
  
_donechangemood
  cpu_mood_counter = rand / 4
_keepdoingwhatyourdoing
  cpu_mood_counter = cpu_mood_counter - 1  

_cpuSteerTowards
  if player1x > player0x then otherbear_j1left = 1
  if player1x < player0x then otherbear_j1right = 1
  if player1y > player0y then otherbear_j1up = 1
  if player1y < player0y then otherbear_j1down = 1

_endPlayer1Move

  if bear0freaktimer > 0 then bear0freaktimer = bear0freaktimer - 1 : COLUP0 = rand: goto _donefreakbear0
  if joy0fire then COLUP0 = BEAR_BROWN else COLUP0 = TREE_GREEN
_donefreakbear0

  if bear1freaktimer > 0 then bear1freaktimer = bear1freaktimer - 1 : COLUP1 = rand: goto _donefreakbear1
  if otherbear_j1fire then COLUP1 = BEAR_BLUE else COLUP1 = TREE_GREEN
_donefreakbear1

  if bear0freaktimer || bear1freaktimer then AUDV1 = NORMAL_VOLUME: AUDF1 = rand else AUDV1 = 0


  rem BULLET SIZE (AND EVERY OTHER FRICKIN THING WHAT THE HECK)
  NUSIZ0=$20
  NUSIZ1=$20
  
  if joy0left then bear0leftright = REFLECTLEFT  
  if joy0right then bear0leftright = REFLECTRIGHT

  if otherbear_j1left then bear1leftright = REFLECTLEFT 
  if otherbear_j1right then bear1leftright = REFLECTRIGHT 

  REFP0 = bear0leftright
  REFP1 = bear1leftright
  drawscreen

  

  if ! joy0fire then goto _no_bullet0_
    rem if bullet0wasfiring then goto _just_move_bullet0_   
_just_move_bullet0_
    bullet0wasfiring = 1
    goto _done_with_bullet0_
_no_bullet0_
  if joy0right then player0x = player0x + 1 
  if joy0left then player0x = player0x - 1 
  if joy0up then player0y = player0y - 1 : pfscroll down 
  if joy0down then player0y = player0y + 1 : pfscroll up 
  bear0needsToRecharge = 0
    missile0x = player0x
    missile0y = player0y
    bullet0wasfiring = 0
_done_with_bullet0_

  if ! bullet0wasfiring then goto _done_steering_bullet0_
  if bear0needsToRecharge then missile0x = OFFSCREEN_POS : missile0y = OFFSCREEN_POS : goto _done_steering_bullet0_
    
    if joy0right then missile0x = missile0x + 1 
    if joy0left then missile0x = missile0x - 1 
    if joy0up then missile0y = missile0y - 1  
    if joy0down then missile0y = missile0y + 1  

_done_steering_bullet0_


  

  if ! otherbear_j1fire then goto _no_bullet1_
    if bullet1wasfiring then goto _just_move_bullet1_      
_just_move_bullet1_
    bullet1wasfiring = 1
    goto _done_with_bullet1_
_no_bullet1_
  if otherbear_j1right then player1x = player1x + 1 
  if otherbear_j1left then player1x = player1x - 1 
  if otherbear_j1up then player1y = player1y - 1 : pfscroll down 
  if otherbear_j1down then player1y = player1y + 1 : pfscroll up
  bear1needsToRecharge = 0
    missile1x = player1x
    missile1y = player1y
    bullet1wasfiring = 0
_done_with_bullet1_



  if ! bullet1wasfiring then goto _done_steering_bullet1_
  if bear1needsToRecharge then missile1x = OFFSCREEN_POS : missile1y = OFFSCREEN_POS : goto _done_steering_bullet1_
        
    if otherbear_j1right then missile1x = missile1x + 1 
    if otherbear_j1left then missile1x = missile1x - 1 
    if otherbear_j1up then missile1y = missile1y - 1  
    if otherbear_j1down then missile1y = missile1y + 1    

_done_steering_bullet1_


  rem PLAYER BOUNDARIES
  if player0x < 1 then player0x = 158
  if player0x > 158 then player0x = 1
  if player0y < 1 then player0y = 88
  if player0y > 88 then player0y = 1


  if player1x < 1 then player1x = 158
  if player1x > 158 then player1x = 1
  if player1y < 1 then player1y = 88
  if player1y > 88 then player1y = 1


  rem BULLET BOUNDARIES
  if bear0needsToRecharge then goto _done_bullet_boundary_0
  if missile0x < 1 then missile0x = 158
  if missile0x > 158 then missile0x = 1
  if missile0y < 1 then missile0y = 88
  if missile0y > 88 then missile0y = 1
_done_bullet_boundary_0
  if bear1needsToRecharge then goto _done_bullet_boundary_1
  if missile1x < 1 then missile1x = 158
  if missile1x > 158 then missile1x = 1
  if missile1y < 1 then missile1y = 88
  if missile1y > 88 then missile1y = 1
_done_bullet_boundary_1
 
 
 
  if collision(player1, missile0) &&         joy0fire && ! bear1freaktimer then bear1freaktimer = 30 : missile0x = player0x : missile0y = player0y : bear0needsToRecharge = 1 : player0score = addbcd(player0score, 1) : cpu_mood_counter = 0
  if collision(player0, missile1) && otherbear_j1fire && ! bear0freaktimer then bear0freaktimer = 30 : missile1x = player1x : missile1y = player1y : bear1needsToRecharge = 1 : player1score = addbcd(player1score, 1) : cpu_mood_counter = 0


  
  if game_is_finished then goto _done_with_endgame_check_
  if player0score > 9 then  game_is_finished = 1
  if player1score > 9 then game_is_finished = 1
  if player0score >= player1score then winnerbearcolor = BEAR_BROWN else winnerbearcolor = BEAR_BLUE
_done_with_endgame_check_  
  if ! game_is_finished then goto _not_ending_game_now_
  if bear0freaktimer || bear1freaktimer then goto _not_ending_game_now_
  goto __init_endscreen__
_not_ending_game_now_

  rem TIME TO UPDATE NOTE?
  if musicTimer = 0 then gosub changeMusicNote
  musicTimer = musicTimer - 1

  
  goto main


_main_ending_
  
  just_a_timer = just_a_timer - 1
  
  if ! just_a_timer then just_a_timer = TIME_BETWEEN_ENDING_SCROLL : COLUPF = rand

  COLUP0 = winnerbearcolor


  player0x = player0x + winningbeardir
  
  if player0x > 110 then winningbeardir = -1 : bear0leftright = REFLECTLEFT 
  
  if player0x < 40 then winningbeardir = 1 : bear0leftright = REFLECTRIGHT  
     
  REFP0 = bear0leftright
  drawscreen

 if musicTimer = 0 then goto changeMusicNoteVictory ;we might break out of the loop so goto not gosub
__return_from__changeMusicNoteVictory__  
 musicTimer = musicTimer - 1

 if beatTimer = 0 then gosub changeBeatNoteVictory
 beatTimer = beatTimer - 1

  goto main


__init_game__

   playfield:
   X.X.X..XXXXXX.XX.X..X..XXXXX...X
   .XXX...X...XXXXXX.XX.XXXXX.X.XX.
   .XXXXX..XXX..X......X..X....X.X.
   ..XXXXX..XX.....XX.XX.X.X.XXX...
   XXX.X.X....X...X.......X.....X.X
   ....XX..XX...XXX.X.X..X...X.X.X.
   .X.XXX....X...XXX.XX..X...X....X
   ...X...X.XX.X.X.X.XX.XXXX...XXXX
   .X.X.X.XX.X..X...X..XX....XX.XX.
   XXXX.XX..XX..XXX..XX.XX..X..XXX.
   XXX.XXXX.X...X....XXX.XXXXXX.XXX
   X....XX...XX..XXX.X...XXX...X..X
end
  game_state_mode = MODE_GAME

  CTRLPF = PLAYERSBEHIND

  player0score = 0
  player1score = 0


  bear0leftright = REFLECTRIGHT  
  bear1leftright = REFLECTLEFT 


  player0x = 50 : player0y = 55
  player1x = 110 : player1y = 55
  missile0x = 50 : missile0y = 50
  missile1x = 70 : missile1y = 70

  COLUPF = TREE_GREEN

  bear0freaktimer = 0
  bear1freaktimer = 0

  game_is_finished = 0

  gosub _reset_music_   
  AUDC0=10
  AUDC1=8  
  
  
  
 goto main
 
__init_endscreen__
  pfclear

  if !cpu_in_control then goto _victoryPlayField
  if player0score >= player1score then goto _victoryPlayField


  rem YOU LOSE!
  playfield: 
  ................................
  ................................
  ................................
  X...X..X..X.X..X....X...XX.XXX.X
  .X.X..X.X.X.X..X...X.X.XX..X...X
  ..X...X.X.X.X..X...X.X..XX.XX...
  ..X....X..XXX..XXX..X..XX..XXX.X
end
  goto _endFinishPlayfields

_victoryPlayField
  rem VICTORY!
  playfield: 
  ................................
  ................................
  ................................
  X.....X.X..XX.XXX..X..XXX.X.X.X.
  .X...X..X.X....X..X.X.X.X.X.X.X.
  ..X.X...X.X....X..X.X.XX...X....
  ...X....X..XX..X...X..X.X..X..X.
end

_endFinishPlayfields


  game_state_mode = MODE_END

  bear0leftright = REFLECTRIGHT  
  bear1leftright = REFLECTLEFT 


  player0x = 70 : player0y = 80
  player1x = OFFSCREEN_POS : player1y = OFFSCREEN_POS
  missile0x = OFFSCREEN_POS : missile0y = OFFSCREEN_POS
  missile1x = OFFSCREEN_POS : missile1y = OFFSCREEN_POS
  
  ;player0x = 10 : player0y = 10
  ;player1x = OFFSCREEN_POS : player1y = OFFSCREEN_POS

  winningbeardir = 1

  
  gosub _reset_music_   
  AUDC0=5
  AUDC1=13  
  
  just_a_timer = TIME_BETWEEN_ENDING_SCROLL
  
 goto main
 
__init_titlescreen__

  rem we always come back to here after a game
  rem so it's good for resetting everything
  cpu_mood = CPU_FIRE
  cpu_mood_counter = 0

  CTRLPF = PLAYERSINFRONT

  playfield:
  X....XX...X..XX..XXX.XX....X.X.
  X...X..X.X.X.X.X.X...X.X...X.X.
  X...X..X.XXX.X.X.XX..X.X...XXXX
  XXX..XX..X.X.XX..XXX.XX......X.
  ...............................
  XXXXXX...XXXXX...XXXX...XXXXX..
  XXX...X..XX.....XX..XX..XX...X.
  XXXXXX...XXXX...XXXXXX..XXXXX..
  XXX...X..XX.....XX..XX..XX..XX.
  XXXXXX...XXXXX..XX..XX..XX...XX
end


  COLUPF = TREE_GREEN

  game_state_mode = MODE_TITLE
  
  gosub _reset_music_ 
  AUDC0=1
  AUDC1=8
 goto main
 

_reset_music_
    musicPointer=0
    musicTimer=0
    beatPointer=0
    beatTimer=0
    AUDV0=0
    
    AUDV1=0
    
  return
 
changeMusicNote
  
  AUDF0 = musicData[musicPointer]
  if musicData[musicPointer] = $FF then AUDV0 = 0 else AUDV0 = NORMAL_VOLUME
  musicPointer = musicPointer + 1
  musicTimer = musicData[musicPointer]
  rem value is (2 * #_OF_NOTES) - 1
  musicPointer = musicPointer + 1
  if musicPointer > 187 then musicPointer = 0

 return


changeMusicNoteTitle
  AUDF0 = musicDataTitle[musicPointer]
  if musicDataTitle[musicPointer] = $FF then AUDV0 = 0 else AUDV0 = NORMAL_VOLUME
  musicPointer = musicPointer + 1
  musicTimer = musicDataTitle[musicPointer]
  rem value is (2 * #_OF_NOTES) - 1
  musicPointer = musicPointer + 1
  if musicPointer > 117 then musicPointer = 0

 return

changeBeatNoteTitle  
  AUDF1 = beatDataTitle[beatPointer]
  if beatDataTitle[beatPointer] = $FF then AUDV1 = 0 else AUDV1 = NORMAL_VOLUME
  beatPointer = beatPointer + 1
  beatTimer = beatDataTitle[beatPointer]
  rem value is (2 * #_OF_NOTES) - 1
  beatPointer = beatPointer + 1
  if beatPointer > 38 then beatPointer = 0
 return

changeMusicNoteVictory
  
  AUDF0 = musicDataVictory[musicPointer]

  if musicDataVictory[musicPointer] = $FF then AUDV0 = 0 else AUDV0 = NORMAL_VOLUME

  musicPointer = musicPointer + 1
  musicTimer = musicDataVictory[musicPointer]
  rem value is (2 * #_OF_NOTES) - 1
  musicPointer = musicPointer + 1
  if musicPointer > 58 then goto __init_titlescreen__
  goto __return_from__changeMusicNoteVictory__  ;we might break out of the loop so goto not gosub
 return

changeBeatNoteVictory
  
  AUDF1 = beatDataVictory[beatPointer]  
  if beatDataVictory[beatPointer] = $FF then AUDV1 = 0 else AUDV1 = NORMAL_VOLUME
  beatPointer = beatPointer + 1
  beatTimer = beatDataVictory[beatPointer]
  rem value is (2 * #_OF_NOTES) - 1
  beatPointer = beatPointer + 1
  if beatPointer > 58 then beatPointer = 0

 return

 data musicData
  17,4,-1,4,17,4,-1,4,15,8,17,4,-1,4,14,4,-1,4,17,4,-1,4
  17,4,-1,4,17,4,-1,4,15,8,17,4,-1,4,14,4,11,4,9,4,7,4
  17,4,-1,4,17,4,-1,4,15,8,17,4,-1,4,14,4,-1,4,11,4,-1,4
  8,4,-1,4,11,4,-1,4,14,8,9,4,-1,4,14,4,-1,4,15,4,-1,4
  17,4,-1,4,17,4,-1,4,15,8,17,4,-1,4,14,4,-1,4,17,4,-1,4
  17,4,-1,4,17,4,-1,4,15,8,17,4,-1,4,14,4,11,4,9,4,7,4
  17,4,-1,4,17,4,-1,4,15,8,17,4,-1,4,14,4,-1,4,11,4,-1,4
  8,4,-1,4,11,4,-1,4,14,8,9,4,-1,4,14,4,-1,4,15,4,-1,4
  19,6,17,76,-1,4,19,6,17,76,-1,4
  
end

 rem THE END IS A LITTLE SCREWY, USING IT AS A TIMER TO GO BACK TO TITLE
 rem GETTING SOME ODD RESULTS BUT WHATEVS
 
 data musicDataVictory
  23,8,-1,8,23,2,-1,4,23,2,-1,4,23,2,-1,4,19,28,-1,4
  19,8,-1,8,19,2,-1,4,19,2,-1,4,19,2,-1,4,15,16,
  11,2,-1,4,7,2,-1,4,9,2,-1,4,8,24,
  -1,150,-1,100,-1,100,-1,100

end

 data beatDataVictory
  23,8,-1,8,23,2,-1,4,23,2,-1,4,23,2,-1,4,23,28,-1,4
  23,8,-1,8,23,2,-1,4,23,2,-1,4,23,2,-1,4,19,16,
  23,2,-1,4,19,2,-1,4,15,2,-1,4,17,24,
  -1,150,-1,100,-1,100,-1,100
end

 data musicDataTitle
  11,8,-1,8,-1,8,11,8,8,8,9,8,11,8,14,8,11,8,14,8,19,8,-1,8,17,8,-1,24
  11,8,-1,8,-1,8,11,8,8,8,9,8,11,8,14,8,11,8,14,8,19,8,-1,8,-1,8,-1,24
  11,8,-1,8,-1,8,11,8,8,8,9,8,11,8,14,8,11,8,14,8,19,8,-1,8,17,8,-1,24
  17,8,-1,8,15,8,14,8,11,8,-1,8,14,8,15,8,17,8,-1,8,19,8,-1,8,17,8,-1,8,-1,8,8,4,9,4
  
end

 data beatDataTitle
  120,2,-1,14,-1,8,120,4,-1,4,40,2,-1,6,120,2,-1,6,-1,8,-1,8,
  -1,8,120,2,-1,14,120,2,-1,6,40,2,-1,14,120,8,-1,8

end

_frame0_for_0_
  player0:
        %01000100
        %00111100
        %01111100
        %01111111
        %00110100
        %00101000
        %00111100
        %00100100
end
  return

_frame1_for_0_
  player0:        
        %00101000
        %00111100
        %10111101
        %01111110
        %00110100
        %00101000
        %00111100
        %00100100
end
  return
 
_frame0_for_1_
  player1:
        %00101000
        %00111100
        %10111101
        %01111110
        %00110100
        %00101000
        %00111100
        %00100100
end  
  return
 
_frame1_for_1_
  player1:
        %01000100
        %00111100
        %01111100
        %01111111
        %00110100
        %00101000
        %00111100
        %00100100
end  
  return

_frame_ai_for_1_
  player1:
        %10010111
        %10010010
        %10010010
        %11110010
        %10010010
        %10010010
        %10010010
        %01100111
end

 rem should be last lines in game  
 inline playerscores.asm
 inline bcd_math.asm