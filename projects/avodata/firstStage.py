import numpy as np
import math

<<<<<<< HEAD

=======
>>>>>>> fea983c7114fbd2dc4ef097a7eea6c7af74ee937
def factorial(n):
	return int(math.gamma(n + 1))


<<<<<<< HEAD
def n_choose_k(n, k):
=======
def nChoosek(n, k):
>>>>>>> fea983c7114fbd2dc4ef097a7eea6c7af74ee937
	return factorial(n) / (factorial(k) * factorial(n - k))


def find_c_and_a(m):
<<<<<<< HEAD
	# A function that receives a matrix m with size k*n, such that k is a characteristics of
	# professions and n is the number of professions that are compared.
	# The function founds using the List Square algorithm, the vector c with size k*1, and a
	# matrix A, to solve the equation Ax = c.
	# The output is a matrix with size k*n which is estimated, the matrix A with size
	# (num_of_professions Choose 2) * (professions) and the error matrix with size k*n
	# (which is calculated by Ax - c = error)
	num_of_professions = m.shape[1]
	num_of_rows = int(n_choose_k(num_of_professions, 2))
=======
	num_of_professions = m.shape[1]
	num_of_rows = int(nChoosek(num_of_professions, 2))
>>>>>>> fea983c7114fbd2dc4ef097a7eea6c7af74ee937
	A = np.zeros(shape=(num_of_rows, num_of_professions), dtype=np.float32)
	row = 0

	for i in range(num_of_professions):
		for j in range(i+1, num_of_professions):
			A[row][i] = 1
			A[row][j] = -1
			row += 1

<<<<<<< HEAD
	num_of_rows_in_m = np.size(m, 0)
=======
	num_of_rows_in_m = np.size(m,0)
>>>>>>> fea983c7114fbd2dc4ef097a7eea6c7af74ee937
	c_matrix = np.zeros((num_of_rows_in_m, num_of_professions), dtype=np.float32)
	for i in range(m.shape[0]):
		c = np.dot(A, m[i].transpose())
		c_matrix[i] = c

	new_m = np.zeros((num_of_rows_in_m, num_of_professions), dtype=np.float32)
	for i in range(c_matrix.shape[0]):
<<<<<<< HEAD
		row_in_m = np.linalg.lstsq(A, c_matrix[i], None)[0]
		new_m[i] = row_in_m

	Ax = np.dot(A, new_m.transpose())
	errors = np.subtract(Ax, c_matrix.transpose())

	return new_m, A, errors

a = np.array([[-2, -2, 4], [-1, -1, 2]])
M_matrix, A_matrix, E_matrix = find_c_and_a(a)
print("The A matrix: ")
print(A_matrix)
print("The M matrix: ")
print(M_matrix)
print("The Error matrix: ")
print(E_matrix)
=======
		row_in_m = np.linalg.lstsq(A, c_matrix[i], None)[0] # TODO how can i got the error?
		new_m[i] = row_in_m

	Ax = np.dot(A, new_m.transpose())
	errors = np.subtract(Ax,c_matrix.transpose())

	return new_m, A, errors

a = np.array([[-2,-2,4],[-1,-1,2]])
M, A, e = find_c_and_a(a)
print("The A matrix: ")
print(A)
print("The M natrix: ")
print(M)
print("The Error natrix: ")
print(e)
















>>>>>>> fea983c7114fbd2dc4ef097a7eea6c7af74ee937
