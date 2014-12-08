--# -path=.:../prelude:../resourceAbstract:../resourceEnglish

resource myTypesEng = open 
    Predef, Prelude,
    AtomEng, ResourceEng, TypesEng in{

lintype
    ClassL  = CN ** {s42 : Str}; -- {cn:CN; id:Str} not possible; let's hope CN has less than 42 "s"-field
    AttrL = CN;
    QueryL = CN;
    Inst = NP; 
    SubtypeL = {s : Str};
    ArgList = {s : Str; empty : Bool};
    ConstraintL = {s:Str};
    ClassConstraintBodyL = {s0, s1 : Str; invC, defC : ENumber};
    OperConstraintBodyL = {s0, s1 : Str; preC, postC : ENumber};
    DocumentL = {s:Str};    
    SentIterL = SentIterO;
    InstIterL = InstIterO;
    AndListL = {s0, s1 : Str; size : ListSize};
    OrListL = {s0, s1 : Str; size : ListSize};
    SumListL = {s0, s1 : Str; size : ListSize};
    ProductListL = {s0, s1 : Str; size : ListSize};
    LetDefL = {
    -- we would like e.g. {s0 : NP; s1 : NP}, which is not possible
    -- instead we just store Strs corresponding to the NPs in nominative case
        s0 : Str; -- definiendum
        s1 : Str  -- definiens
    };
    LetDefSentL = {
    -- we might want {s0 : S; s1 : S}, which is not possible
    -- so we go with Strs
        s0 : Str; -- definiendum
        s1 : Str  -- definiens
    };
    LetDefListL = {
        s : Str;
        length : ENumber
    };
    --   NP     = {s : NPForm => Str ; n : Number ; p : Person} ;
    InstListL = { s0, s1 : NPForm => Str};


oper
    SentIterO : Type = S ** {s1 : Str; moreThanOne : Bool};
    InstIterO : Type = Inst ** {s1 : Str};

param
    ListSize = LSempty | LSone | LSmany;    

oper
    np2inst : NP -> Inst = \np -> np;    
    inst2np : Inst -> NP = \inst -> inst;
    

};
