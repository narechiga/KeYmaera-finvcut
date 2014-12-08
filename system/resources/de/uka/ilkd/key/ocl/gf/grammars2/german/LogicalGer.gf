--# -path=.:../resourceAbstract:../prelude:../resourceGerman

-- Slightly ad hoc and formal negation and connectives.

resource LogicalGer = PredicationGer ** open ResourceGer, ParadigmsGer in {

  oper
    negS : S -> S ;         -- es ist nicht der Fall, dass S
    univS : CN -> S -> S ;  -- f�r alle CNs gilt es, dass S
    existS : CN -> S -> S ; -- es gibt ein CN derart, dass S
    existManyS : CN -> S -> S ; -- es gibt CNs derart, dass S
--.

    negS = \A -> 
      PredVP ItNP (NegNP (DefOneNP (CNthatS (UseN (nRaum "Fall" "Faelle")) A))) ;
    univS = \A,B ->
      PredVP ItNP 
        (AdvVP (PosVS (mkV "gelten" "gilt" "gelte" "gegolten" ** {lock_VS = <>}) B)
                    (mkPP accusative "f�r" (DetNP AllDet A))) ;
    existS = \A,B ->
      PredVP ItNP (PosTV (tvDir (mkV "geben" "gibt" "gib" "gegeben"))
                     (IndefOneNP (ModRC A (RelSuch B)))) ;
    existManyS = \A,B ->
     PredVP ItNP (PosTV (tvDir (mkV "geben" "gibt" "gib" "gegeben"))
                    (IndefManyNP (ModRC A (RelSuch B)))) ;
} ;
