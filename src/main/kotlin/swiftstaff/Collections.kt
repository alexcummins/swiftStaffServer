package swiftstaff

data class Job(val _id: String? = null, val hourlyRate: String = "1075", val date: String = "", val startTime: String = "", val endTime: String = "", val sendStrategy: String = "1", val extraInfo : String = "" )
data class Jobs(val count: Int, val jobsList: MutableList<Job>)
