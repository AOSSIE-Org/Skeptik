package at.logic.skeptik.judgment 

import at.logic.skeptik.expression._
import at.logic.skeptik.util.unicode._
  

case class NamedE(name:String, expression:E) {
  def logicalSize = expression.logicalSize + 1
  override def toString = name + ": " + expression
}

class NaturalSequent(val context: Set[NamedE], val e:E) extends Judgment {
	
  override def equals(v:Any) = v match {		
      case that:NaturalSequent => (that canEqual this) && (context == that.context) && (e == that.e)	
      case _ => false		
  }		
  def canEqual(other: Any) = other.isInstanceOf[NaturalSequent]
  
  def subsumes(j: Judgment) = j.isInstanceOf[NaturalSequent] && 
                              j.asInstanceOf[NaturalSequent].e == e && 
                              (context subsetOf j.asInstanceOf[NaturalSequent].context)

  def logicalSize = e.logicalSize
  def msize = 10 * context.size + e.logicalSize
  //def msize = (context.map(_.expression.logicalSize) :\ 0)(_ + _ + 1) + e.logicalSize
  
  override def hashCode = 42*context.hashCode + e.hashCode
  override def toString = context.mkString(", ") + unicodeOrElse(" \u22A2 "," :- ") + e
}


