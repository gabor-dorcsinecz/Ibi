package model

import reactivemongo.api.bson.BSONObjectID

final case class Quiz(
                       _id: BSONObjectID = BSONObjectID.generate(),
                       question: String,
                       answers: List[String],
                       correctAnswer:Int   //one element from the above list starting from zero
                     )

final case class QuizAnswer(
                              user: User,
                              quiz: Quiz,
                              answer: Int
                            )