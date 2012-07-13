package at.logic.skeptik.algorithm.compressor

import at.logic.skeptik.proof.sequent._
import at.logic.skeptik.proof.ProofNodeCollection
import at.logic.skeptik.proof.sequent.lk._
import at.logic.skeptik.judgment._
import at.logic.skeptik.judgment.immutable.{SetSequent => IClause}
import at.logic.skeptik.expression._
import collection.mutable.{HashMap => MMap}
import collection.Map


abstract class RecyclePivots
extends AbstractRPIAlgorithm with CollectEdgesUsingSafeLiterals {
  def apply(proof: SequentProof): SequentProof = {
    val nodeCollection = ProofNodeCollection(proof)
    val edgesToDelete = collectEdgesToDelete(nodeCollection)
    if (edgesToDelete.isEmpty) proof else nodeCollection.foldDown(fixProofs(edgesToDelete))
  }
}

trait outIntersection
extends AbstractRPIAlgorithm {
  def computeSafeLiterals(proof: SequentProof,
                          childrensSafeLiterals: List[(SequentProof, IClause)],
                          edgesToDelete: Map[SequentProof,DeletedSide],
                          safeLiteralsFromChild: ((SequentProof, IClause)) => IClause
                          ) : IClause =
    if (childrensSafeLiterals.length == 1)
      safeLiteralsFromChild(childrensSafeLiterals.head)
    else
      IClause()
}

trait MinConclusionHeuristic
extends AbstractRPILUAlgorithm {
  def heuristicChoose(left: SequentProof, right: SequentProof):SequentProof = {
    def sequentSize(s: Sequent) = s.ant.length + s.suc.length
    if (sequentSize(left.conclusion) < sequentSize(right.conclusion)) left else right
  }
}

trait MinProofHeuristic
extends AbstractRPILUAlgorithm {
  def heuristicChoose(left: SequentProof, right: SequentProof):SequentProof = {
    if (ProofNodeCollection(left).size < ProofNodeCollection(right).size) left else right
  }
}