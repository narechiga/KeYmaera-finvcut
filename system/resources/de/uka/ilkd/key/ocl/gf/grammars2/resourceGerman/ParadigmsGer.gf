--# -path=.:../abstract:../../prelude

--1 German Lexical Paradigms
--
-- Aarne Ranta 2003
--
-- This is an API to the user of the resource grammar 
-- for adding lexical items. It give shortcuts for forming
-- expressions of basic categories: nouns, adjectives, verbs.
-- 
-- Closed categories (determiners, pronouns, conjunctions) are
-- accessed through the resource syntax API, $Resource.gf$. 
-- Their original typings via abstract syntax are in
-- $Structural.gf$, which also contains documentation.
--
-- The main difference with $MorphoGer.gf$ is that the types
-- referred to are compiled resource grammar types. We have moreover
-- had the design principle of always having existing forms, not stems, as string
-- arguments of the paradigms.
--
-- The following modules are presupposed:

resource ParadigmsGer = 
  open Prelude, (Morpho=MorphoGer), SyntaxGer, ResourceGer in {


--2 Parameters 
--
-- To abstract over gender names, we define the following identifiers.

oper
  Gender    : Type ; 

  masculine : Gender ;
  feminine  : Gender ;
  neuter    : Gender ;

-- To abstract over case names, we define the following.

  Case    : Type ; 

  nominative : Case ;
  accusative : Case ;
  dative     : Case ;
  genitive   : Case ;

-- To abstract over number names, we define the following.

  Number    : Type ; 

  singular : Number ;
  plural   : Number ;


--2 Nouns

-- Worst case: give all four singular forms, two plural forms (others + dative),
-- and the gender.

  mkN  : (_,_,_,_,_,_ : Str) -> Gender -> N ; 
                                 -- mann, mann, manne, mannes, m�nner, m�nnern

-- Often it is enough with singular and plural nominatives, and singular
-- genitive. The plural dative
-- is computed by the heuristic that it is the same as the nominative this
-- ends with "n" or "s", otherwise "n" is added.

  nGen : Str -> Str -> Str ->  Gender -> N ; -- punkt,punktes,punkt
  
-- Here are some common patterns. Singular nominative or two nominatives are needed.
-- Two forms are needed in case of Umlaut, which would be complicated to define.
-- For the same reason, we have separate patterns for multisyllable stems.
-- 
-- The weak masculine pattern $nSoldat$ avoids duplicating the final "e".

  nRaum   : (_,_ : Str) -> N ;    -- Raum, (Raumes,) R�ume (masc)
  nTisch  : Str -> N ;            -- Tisch, (Tisches, Tische) (masc)
  nVater  : (_,_ : Str) -> N ;    -- Vater, (Vaters,) V�ter (masc)
  nFehler : Str -> N ;            -- Fehler, (fehlers, Fehler) (masc)
  nSoldat : Str -> N ;            -- Soldat (, Soldaten) ; Kunde (, Kunden) (masc)

-- Neuter patterns. 

  nBuch   : (_,_ : Str) -> N ;    -- Buch, (Buches, B�cher) (neut)
  nMesser : Str -> N ;            -- Messer, (Messers, Messer) (neut)
  nBein   : Str -> N ;            -- Bein, (Beins, Beine) (neut)
  nAuto   : Str -> N ;            -- Auto, (Autos, Autos) (neut)

-- Feminine patterns. Duplicated "e" is avoided in $nFrau$.

  nStudentin : Str -> N ;         -- Studentin (Studentinne) 
  nHand   : (_,_ : Str) -> N ;    -- Hand, H�nde; Mutter, M�tter (fem)
  nFrau   : Str -> N ;            -- Frau (, Frauen) ; Wiese (, Wiesen) (fem)


-- Nouns used as functions need a preposition. The most common is "von".

  mkFun  : N -> Preposition -> Case -> Fun ;
  funVon : N -> Fun ;

-- Proper names, with their possibly
-- irregular genitive. The regular genitive is  "s", omitted after "s".

  mkPN  : (karolus, karoli : Str) -> PN ; -- karolus, karoli
  pnReg : (Johann : Str) -> PN ;          -- Johann, Johanns ; Johannes, Johannes

-- On the top level, it is maybe $CN$ that is used rather than $N$, and
-- $NP$ rather than $PN$.

  mkCN  : N -> CN ;
  mkNP  : (karolus,karoli : Str) -> NP ;

  npReg : Str -> NP ;   -- Johann, Johanns

-- In some cases, you may want to make a complex $CN$ into a function.

  mkFunCN  : CN -> Preposition -> Case -> Fun ;
  funVonCN : CN -> Fun ;


--2 Adjectives

-- Non-comparison one-place adjectives need two forms in the worst case:
-- the one in predication and the one before the ending "e".

  mkAdj1 : (teuer,teur : Str) -> Adj1 ;

-- Invariable adjective are a special case.

  adjInvar : Str -> Adj1 ;          -- prima

-- The following heuristic recognizes the the end of the word, and builds
-- the second form depending on if it is "e", "er", or something else.
-- N.B. a contraction is made with "er", which works for "teuer" but not
-- for "bitter".

  adjGen : Str -> Adj1 ;            -- gut; teuer; b�se

-- Two-place adjectives need a preposition and a case as extra arguments.

  mkAdj2 : Adj1 -> Str -> Case -> Adj2 ;  -- teilbar, durch, acc

-- Comparison adjectives may need three adjective, corresponding to the
-- three comparison forms. 

  mkAdjDeg : (gut,besser,best : Adj1) -> AdjDeg ;

-- In many cases, each of these adjectives is itself regular. Then we only
-- need three strings. Notice that contraction with "er" is not performed
-- ("bessere", not "bessre").

  aDeg3 : (gut,besser,best : Str) -> AdjDeg ;

-- In the completely regular case, the comparison forms are constructed by
-- the endings "er" and "st".

  aReg : Str -> AdjDeg ;    -- billig, billiger, billigst

-- The past participle of a verb can be used as an adjective.

  aPastPart : V -> Adj1 ;   -- gefangen

-- On top level, there are adjectival phrases. The most common case is
-- just to use a one-place adjective. The variation in $adjGen$ is taken
-- into account.

  apReg : Str -> AP ;

--OLD:
--2 Verbs
--
-- The fragment only has present tense so far, but in all persons.
-- It also has the infinitive and the past participles.
-- The worst case macro needs four forms: : the infinitive and 
-- the third person singular (where Umlaut may occur), the singular imperative,
-- and the past participle.
-- 
-- The function recognizes if the stem ends with "s" or "t" and performs the
-- appropriate contractions.

--NEW (By Harald Hammarstr�m):
--2 Verbs
-- The worst-case macro needs six forms:
-- x Infinitive, 
-- x 3p sg pres. indicative, 
-- x 2p sg imperative, 
-- x 1/3p sg imperfect indicative, 
-- x 1/3p sg imperfect subjunctive (because this uncommon form can have umlaut)
-- x the perfect participle 

-- But you'll only want to use one of the five macros:
-- x weakVerb              -- For a regular verb like legen
-- x verbGratulieren       -- For a regular verb without ge- in the perfect
--                            particple. Like gratulieren, beweisen etc 
-- x verbStrongSingen      -- A strong verb without umlauting present tense.
--                            You'll need to supply the strong imperfect forms
--                            as well as the participle. 
-- x verbStrongSehen       -- A strong verb that umlauts in the 2/3p sg pres
--                            indicative as well as the imperative. You'll
--                            need to give (only) the 3rd p sg pres ind. in
--                            addition to the strong imperfect forms and the
--                            part participle.
-- x verbStrongLaufen      -- A strong verb that umlauts in the 2/3p sg pres
--                            indicative but NOT the imperative. You'll
--                            need to give (only) the 3rd p sg pres ind. in
--                            addition to the strong imperfect forms and the
--                            part participle.
--
-- Things that are handled automatically
-- x Imperative e (although optional forms are not given)
-- x Extra e in verbs like arbeitete, regnet, findet, atmet.
--   NOTE: If pres. umlauting strong verbs are defined through the verbumStrong
--   macro (which they should) it is automatically handled so they avoid 
--   falling into this rule e.g er tritt (rather than *er tritet)
-- x s is dropped in the 2p sg if appropriate du setzt
-- x verbs that end in -rn, -ln rather than -en

-- Things that are not handled:
-- x -�-/-ss-
-- x Optional dropping of -e- in e.g wand(e)re etc
-- x Optional indicative forms instead of pres. subj. 2p sg. and 2p pl.  
-- x (Weak) verbs without the ge- on the participle (in wait for a systematic
--   treatment of the insep. prefixes and stress). You have to manually use
--   the verbGratulieren for this. E.g do verbGratulieren "beweisen" - 
--   verbWeak "beweisen" would yield *gebeweist.

  mkV : (_,_,_,_,_,_ : Str) -> V ;   -- geben, gibt, gib, gab, g�be, gegeben

-- Weak verbs are sometimes called regular verbs.
  
  vWeak  : Str -> V ;             -- f�hren

  vGratulieren : Str -> V ;             -- gratulieren
  vSehen : (_,_,_,_,_ : Str) -> V ; -- sehen, sieht, sah, s�he, gesehen
  vLaufen : (_,_,_,_,_ : Str) -> V ; -- laufen, lauft, liefe, liefe, gelaufen

-- The verbs 'be' and 'have' are special.

  vSein  : V ;
  vHaben : V ;

-- Some irregular verbs.

  vFahren : V ;

-- Verbs with a detachable particle, with regular ones as a special case.

  vPartWeak  : (_,_ : Str) -> V ;             -- f�hren, aus

--  vPartGratulieren (_,_ : Str) -> V ;          
  vPartSehen : (_,_,_,_,_,_ : Str) -> V ; -- sehen, sieht, sah, s�he, gesehen
  vPartLaufen : (_,_,_,_,_,_ : Str) -> V ; -- laufen, lauft, liefe, liefe, gelaufen
  mkVPart  :  V -> Str -> V ;              -- vFahren, aus

-- Obsolete; use vPartWeak etc instead
  --vPart    :  (_,_,_,_,_ : Str) -> V ;   -- sehen, sieht, sieh, gesehen, aus
  --vPartReg :  (_,_     : Str) -> V ;     -- bringen, um

-- Two-place verbs, and the special case with direct object. Notice that
-- a particle can be included in a $V$.

  mkTV     : V   -> Str -> Case -> TV ;    -- h�ren, zu, dative

  tvWeak    : Str -> Str -> Case -> TV ;    -- h�ren, zu, dative
  tvDir    : V -> TV ;                     -- umbringen
  tvDirReg : Str -> TV ;                   -- lieben

-- Three-place verbs require two prepositions and cases.

  mkV3 : V -> Str -> Case -> Str -> Case -> V3 ;  -- geben,[],dative,[],accusative

-- Sentence-complement verbs are just verbs.

  mkVS : V -> VS ;

-- Verb-complement verbs either need the "zu" particle or don't.
-- The ones that don't are usually auxiliary verbs.

  vsAux : V -> VV ;
  vsZu  : V -> VV ;

--2 Adverbials
--
-- Adverbials for modifying verbs, adjectives, and sentences can be formed 
-- from strings.

  mkAdV : Str -> AdV ;
  mkAdA : Str -> AdA ;
  mkAdS : Str -> AdS ;

-- Prepositional phrases are another productive form of adverbials.

  mkPP : Case -> Str -> NP -> AdV ;

-- One can also use the function $ResourceGer.PrepNP$ with one of the given
-- prepositions or a preposition formed by giving a string and a case:

  mkPrep : Str -> Case -> Prep ;

-- The definitions should not bother the user of the API. So they are
-- hidden from the document.
--.

  Gender = SyntaxGer.Gender ;
  Case = SyntaxGer.Case ;
  Number = SyntaxGer.Number ;

  masculine = Masc ;
  feminine  = Fem ;
  neuter = Neut ;
  nominative = Nom ;
  accusative = Acc ;
  dative = Dat ;
  genitive = Gen ;
  -- singular defined in Types
  -- plural defined in Types

  mkN a b c d e f g = mkNoun a b c d e f g ** {lock_N = <>} ;

  nGen = \punkt, punktes, punkte, g -> let {
      e   = Predef.dp 1 punkte ; 
      eqy = ifTok N e ;
      noN = mkNoun4 punkt punktes punkte punkte g ** {lock_N = <>}
    } in
    eqy "n" noN (
    eqy "s" noN (
            mkNoun4 punkt punktes punkte (punkte+"n") g ** {lock_N = <>})) ;

  nRaum = \raum, r�ume -> nGen raum (raum + "es") r�ume masculine ;
  nTisch = \tisch -> 
    mkNoun4 tisch (tisch + "es") (tisch + "e") (tisch +"en") masculine ** 
    {lock_N = <>};
  nVater = \vater, v�ter -> nGen vater (vater + "s") v�ter masculine ;
  nFehler = \fehler -> nVater fehler fehler ;

  nSoldat = \soldat -> let {
     e = Predef.dp 1 soldat ; 
     soldaten = ifTok Tok e "e" (soldat + "n") (soldat + "en")
    } in
    mkN soldat soldaten soldaten soldaten soldaten soldaten masculine ;

  nBein = \bein -> declN2n bein ** {lock_N = <>};
  nBuch = \buch, b�cher -> nGen buch (buch + "es") b�cher neuter ;
  nMesser = \messer -> nGen messer (messer + "s") messer neuter ;
  nAuto = \auto -> let {autos = auto + "s"} in 
          mkNoun4 auto autos autos autos neuter ** {lock_N = <>} ;

  nStudentin = \studentin -> declN1in studentin ** {lock_N = <>}; 
  nHand = \hand, h�nde -> nGen hand hand h�nde feminine ;

  nFrau = \frau -> let {
     e = Predef.dp 1 frau ; 
     frauen = ifTok Tok e "e" (frau + "n") (frau + "en")
    } in
    mkN frau frau frau frau frauen frauen feminine ;

  mkFun n = mkFunCN (UseN n) ;
  funVon n = funVonCN (UseN n) ;

  mkPN = \karolus, karoli -> 
    {s = table {Gen => karoli ; _ => karolus} ; lock_PN = <>} ;
  pnReg = \horst -> 
    mkPN horst (ifTok Tok (Predef.dp 1 horst) "s" horst (horst + "s")) ;

  mkCN = UseN ;
  mkNP = \x,y -> UsePN (mkPN x y) ;
  npReg = \s -> UsePN (pnReg s) ;

  mkFunCN n p c = mkFunC n p c ** {lock_Fun = <>} ;
  funVonCN n = funVonC n ** {lock_Fun = <>} ;

  mkAdj1 x y = mkAdjective x y ** {lock_Adj1 = <>} ;
  adjInvar a = Morpho.adjInvar a ** {lock_Adj1 = <>} ;
  adjGen a = Morpho.adjGen a ** {lock_Adj1 = <>} ;
  mkAdj2 = \a,p,c -> a ** {s2 = p ; c = c ; lock_Adj2 = <>} ;

  mkAdjDeg a b c = mkAdjComp a b c ** {lock_AdjDeg = <>} ;
  aDeg3 a b c = adjCompReg3 a b c ** {lock_AdjDeg = <>} ;
  aReg a = adjCompReg a ** {lock_AdjDeg = <>} ;
  aPastPart = \v -> {s = table AForm {a => v.s ! VPart a} ; lock_Adj1 = <>} ; 
  apReg = \s -> AdjP1 (adjGen s) ;

  mkV a b c d e f = mkVerbSimple (mkVerbum a b c d e f) ** {lock_V = <>} ;
  vWeak a = mkVerbSimple (verbumWeak a) ** {lock_V = <>} ;
  vGratulieren a = mkVerbSimple (verbumGratulieren a) ** {lock_V = <>} ; 
  vSehen a b c d e = mkVerbSimple (verbumStrongSehen a b c d e) ** {lock_V = <>} ;
  vLaufen a b c d e = mkVerbSimple (verbumStrongLaufen a b c d e) ** {lock_V = <>} ;

  -- vReg = \s -> mkVerbSimple (regVerb s) ** {lock_V = <>} ;
  vSein = verbSein ** {lock_V = <>} ;
  vHaben = verbHaben ** {lock_V = <>} ;
  vFahren = mkVerbSimple (verbumStrongLaufen "fahren" "f�hrt" "fuhr" "f�hre" "gefahren") ** {lock_V = <>} ;

  vPartWeak = \f�hren, aus -> (mkVerb (verbumWeak f�hren) aus) ** {lock_V = <>} ;
  --vGratulieren = verbumGratulieren ** {lock_V = <>} ; 
  vPartSehen a b c d e aus = (mkVerb (verbumStrongSehen a b c d e) aus) ** {lock_V = <>} ;
  vPartLaufen a b c d e aus = (mkVerb (verbumStrongLaufen a b c d e) aus) ** {lock_V = <>} ;

  --vPart = \sehen, sieht, sieh, gesehen, aus -> 
  --  mkVerb (mkVerbum sehen sieht sieh gesehen) aus ** {lock_V = <>} ;
  --vPartReg = \sehen, aus -> mkVerb (regVerb sehen) aus ** {lock_V = <>} ;
  mkVPart v p = mkVerb v.s p  ** {lock_V = <>} ;

  mkTV v p c = mkTransVerb v p c  ** {lock_TV = <>} ;
  tvWeak = \h�ren, zu, dat -> mkTV (vWeak h�ren) zu dat ;
  tvDir = \v -> mkTV v [] accusative ;
  tvDirReg = \v -> tvWeak v [] accusative ; 
  mkV3 v s c t d = mkDitransVerb v s c t d ** {lock_V3 = <>} ;

  mkVS v = v ** {lock_VS = <>} ;
  vsAux v = v ** {isAux = True ; lock_VV = <>} ; 
  vsZu v = v ** {isAux = True ; lock_VV = <>} ; 

  mkAdV a = ss a ** {lock_AdV = <>} ;
  mkPP x y = PrepNP {s = y ; c = x ; lock_Prep = <>} ;
  mkAdA a = ss a ** {lock_AdA = <>} ;
  mkAdS a = ss a ** {lock_AdS = <>} ;
  mkPrep s c = {s = s ; c = c ; lock_Prep = <>} ;

} ;
