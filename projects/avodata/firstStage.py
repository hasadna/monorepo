import numpy as np
import math

def factorial(n):
	return int(math.gamma(n + 1))


def nChoosek(n, k):
	return factorial(n) / (factorial(k) * factorial(n - k))


def find_c_and_a(m):
	num_of_professions = m.shape[1]
	num_of_rows = int(nChoosek(num_of_professions, 2))
	A = np.zeros(shape=(num_of_rows, num_of_professions), dtype=np.float32)
	row = 0

	for i in range(num_of_professions):
		for j in range(i+1, num_of_professions):
			A[row][i] = 1
			A[row][j] = -1
			row += 1

	num_of_rows_in_m = np.size(m,0)
	c_matrix = np.zeros((num_of_rows_in_m, num_of_professions), dtype=np.float32)
	for i in range(m.shape[0]):
		c = np.dot(A, m[i].transpose())
		c_matrix[i] = c

	new_m = np.zeros((num_of_rows_in_m, num_of_professions), dtype=np.float32)
	for i in range(c_matrix.shape[0]):
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
















