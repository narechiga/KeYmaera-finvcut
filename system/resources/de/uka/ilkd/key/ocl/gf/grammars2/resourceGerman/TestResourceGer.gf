-- use this path to read the grammar from the same directory
--# -path=.:../abstract:../../prelude

concrete TestResourceGer of TestResource = StructuralGer ** open SyntaxGer in {

flags startcat=Phr ; lexer=text ; parser=chart ; unlexer=text ;

-- a random sample from the lexicon

lin
  Big = adjCompReg3 "gross" "gr�sser" "gr�sst";
  Small = adjCompReg "klein" ;
  Happy = adjCompReg "gl�cklich" ;
  Old = adjCompReg3 "alt" "�lter" "�ltest";
  Young = adjCompReg3 "jung" "j�nger" "j�ngst";
  American = adjReg "Amerikanisch" ;
  Finnish = adjReg "Finnisch" ;
  Married = adjReg "verheiratet" ** {s2 = "mit" ; c = Dat} ;
  Man = declN2u "Mann" "M�nner" ;
  Woman = declN1 "Frau" ;
  Bottle = declN1e "Flasche" ;
  Wine = declN2 "Wein" ;
  Car = declNs "Auto" ;
  House = declN3uS "Haus" "H�user" ;
  Light = declN3 "Licht" ;
  Bar = declNs "Bar" ;
  Walk = mkVerbSimple (verbumStrongSingen "gehen" "ging" "ginge" "gegangen") ;
  Run = mkVerbSimple (verbumStrongLaufen "laufen" "l�uft" "lief" "liefe" "gelaufen") ; 
  Say = mkVerbSimple (verbumWeak "sagen") ;
  Prove = mkVerbSimple (verbumGratulieren "beweisen") ; --without ge
  Send = mkTransVerb (mkVerbSimple (verbumStrongSingen "senden" "sandte" "s�ndte" "gesandt")) [] Acc;
  Drink = transDir (mkVerbSimple (verbumStrongSingen "trinken" "trank" "tr�nke" "getrunken")) ;
  Love = mkTransVerb (mkVerbSimple (verbumWeak "lieben")) [] Acc ;
  Wait = mkTransVerb (mkVerbSimple (verbumWeak "warten")) "auf" Acc ;
  Give = mkDitransVerb 
           (mkVerbSimple (verbumStrongSehen "geben" "gibt" "gab" "g�be" "gegeben")) [] Dat [] Acc ;
  Prefer = mkDitransVerb 
           (mkVerb (verbumStrongSingen "ziehen" "zog" "z�ge" "gezogen") "vor") [] Acc "vor" Dat ;
  Mother = mkFunC (n2n (declN2uF "Mutter" "M�tter")) "von" Dat ;
  Uncle = mkFunC (n2n (declN2i "Onkel")) "von" Dat ;
  Connection = mkFunC (n2n (declN1 "Verbindung")) "von" Dat ** 
                                     {s3 = "nach" ; c2 = Dat} ;

  Always = mkAdverb "immer" ;
  Well = mkAdverb "gut" ;

  SwitchOn  = mkTransVerb (mkVerb (verbumWeak "schalten") "auf") [] Acc  ;
  SwitchOff = mkTransVerb (mkVerb (verbumWeak "schalten") "aus") [] Acc  ;

  John = mkProperName "Johann" ;
  Mary = mkProperName "Maria" ;

} ;

