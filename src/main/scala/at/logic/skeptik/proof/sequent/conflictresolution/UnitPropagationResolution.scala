package at.logic.skeptik.proof.sequent.conflictresolution

import at.logic.skeptik.algorithm.prover._
import at.logic.skeptik.algorithm.unifier.MartelliMontanari
import at.logic.skeptik.expression.{E, Var}
import at.logic.skeptik.judgment.Sequent
import at.logic.skeptik.judgment.immutable.SetSequent
import at.logic.skeptik.proof.sequent.SequentProofNode

import scala.collection.mutable

/**
  * Represents Unit-Propagation Resolution rule from Conflict Resolution calculus.
  *
  * @author Daniyar Itegulov
  */
class UnitPropagationResolution(val left: Seq[SequentProofNode],
                                val right: SequentProofNode,
                                val desiredIndex: Int)
                               (implicit variables: mutable.Set[Var]) extends SequentProofNode {

  def unify(equations: Iterable[(E, E)]) = MartelliMontanari(equations)

  require(left.forall(_.conclusion.width == 1), "All left conclusions should be unit")
  require(desiredIndex < right.conclusion.width, "Desired should be a literal from right conclusion")
  require(left.size + 1 == right.conclusion.size, "There should be enough left premises to derive desired")
  val leftLiterals = left.map(_.conclusion.literals.head)
  val rightLiterals = right.conclusion.literals.patch(desiredIndex, Nil, 1)
  require(leftLiterals.zip(rightLiterals).forall { case (f, s) => f.negated != s.negated }, "Right and left literals should be opposite")

  val leftAux = leftLiterals.map(_.unit)
  val rightAux = rightLiterals.map(_.unit)

  val (leftMgus, rightMgu) = unifyWithRename(leftAux, rightAux) match {
    case None => throw new Exception("Unit-Propagation Resolution: given premise clauses are not resolvable")
    case Some(u) => u
  }

  override def auxFormulasMap: Map[SequentProofNode, Sequent] =
    (left.map(spn => spn -> spn.conclusion) :+ (right -> rightLiterals.toSequent)).toMap

  override def conclusionContext: Sequent = rightMgu(right.conclusion.literals(desiredIndex))

  override def mainFormulas: Sequent = SetSequent()()

  override def premises: Seq[SequentProofNode] = left :+ right
}
